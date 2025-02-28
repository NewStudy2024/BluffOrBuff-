package BluffOrBluff.simulation;

import BluffOrBluff.model.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class MonteCarloSimulator {
    private static final int DEFAULT_SIMULATIONS = 10000;
    private static final Map<String, Double> resultCache = new ConcurrentHashMap<>();

    private final List<Card> playerCards;
    private final List<Card> knownCommunityCards;
    private final int simulationCount;

    /**
     * Creates a Monte Carlo simulator with adaptive simulation count based on game stage
     */
    public MonteCarloSimulator(List<Card> playerCards, List<Card> communityCards) {
        this(playerCards, communityCards, getOptimalSimulations(communityCards.size()));
    }

    /**
     * Creates a Monte Carlo simulator with custom simulation count
     */
    public MonteCarloSimulator(List<Card> playerCards, List<Card> communityCards, int simulations) {
        this.playerCards = new ArrayList<>(playerCards);
        this.knownCommunityCards = new ArrayList<>(communityCards);
        this.simulationCount = simulations;
    }

    /**
     * Get optimal simulation count based on game stage
     */
    public static int getOptimalSimulations(int communityCardCount) {
        return switch(communityCardCount) {
            case 0 -> 8000;   // Pre-flop
            case 3 -> 11000;   // Flop
            case 4 -> 14000;   // Turn
            case 5 -> 17000;  // River
            default -> 10000;
        };
    }

    /**
     * Generate a cache key based on current cards
     */
    private String generateCacheKey() {
        StringBuilder sb = new StringBuilder();
        playerCards.forEach(card -> sb.append(card.toString()));
        sb.append("|");
        knownCommunityCards.forEach(card -> sb.append(card.toString()));
        return sb.toString();
    }

    /**
     * Estimates the probability of winning based on current cards using parallel processing
     * @return Probability between 0.0 (certain loss) and 1.0 (certain win)
     */
    public double estimateWinProbability() {
        // Check cache first
        String cacheKey = generateCacheKey();
        if (resultCache.containsKey(cacheKey)) {
            return resultCache.get(cacheKey);
        }

        AtomicInteger wins = new AtomicInteger(0);
        AtomicInteger ties = new AtomicInteger(0);

        List<Card> knownCards = new ArrayList<>();
        knownCards.addAll(playerCards);
        knownCards.addAll(knownCommunityCards);

        // Use parallel stream for faster processing
        ForkJoinPool customThreadPool = new ForkJoinPool(
                Math.max(2, Runtime.getRuntime().availableProcessors())
        );

        try {
            customThreadPool.submit(() ->
                IntStream.range(0, simulationCount)
                    .parallel()
                    .forEach(i -> {
                        // Create a fresh deck and remove known cards
                        Deck simDeck = new Deck();
                        simDeck.removeCards(knownCards);

                        // Generate opponent hand
                        List<Card> opponentCards = new ArrayList<>(2);
                        opponentCards.add(simDeck.dealCard());
                        opponentCards.add(simDeck.dealCard());

                        // Complete community cards
                        List<Card> fullCommunityCards = new ArrayList<>(knownCommunityCards);
                        while (fullCommunityCards.size() < 5) {
                            fullCommunityCards.add(simDeck.dealCard());
                        }

                        // Evaluate hands
                        List<Card> playerFullHand = new ArrayList<>(playerCards);
                        playerFullHand.addAll(fullCommunityCards);

                        List<Card> opponentFullHand = new ArrayList<>(opponentCards);
                        opponentFullHand.addAll(fullCommunityCards);

                        HandRank playerHandRank = HandEvaluator.evaluateHand(playerFullHand);
                        HandRank opponentHandRank = HandEvaluator.evaluateHand(opponentFullHand);

                        // Compare hands
                        int comparison = playerHandRank.compareTo(opponentHandRank);
                        if (comparison > 0) wins.incrementAndGet();
                        else if (comparison == 0) ties.incrementAndGet();
                    })
            ).get(); // Wait for completion
        } catch (InterruptedException | ExecutionException e) {
            // Fall back to single-threaded if parallel execution fails
            return runSingleThreaded(knownCards);
        } finally {
            customThreadPool.shutdown();
        }

        double probability = (wins.get() + 0.5 * ties.get()) / simulationCount;

        // Cache results for large simulations
        if (simulationCount >= 5000) {
            resultCache.put(cacheKey, probability);
        }

        return probability;
    }

    /**
     * Fallback method for single-threaded execution
     */
    private double runSingleThreaded(List<Card> knownCards) {
        int wins = 0, ties = 0;

        for (int i = 0; i < simulationCount; i++) {
            // Create a fresh deck and remove known cards
            Deck simDeck = new Deck();
            simDeck.removeCards(knownCards);

            // Generate a random opponent hand
            List<Card> opponentCards = new ArrayList<>();
            opponentCards.add(simDeck.dealCard());
            opponentCards.add(simDeck.dealCard());

            // Complete community cards if needed
            List<Card> fullCommunityCards = new ArrayList<>(knownCommunityCards);
            while (fullCommunityCards.size() < 5) {
                fullCommunityCards.add(simDeck.dealCard());
            }

            // Evaluate and compare hands
            List<Card> playerFullHand = new ArrayList<>(playerCards);
            playerFullHand.addAll(fullCommunityCards);

            List<Card> opponentFullHand = new ArrayList<>(opponentCards);
            opponentFullHand.addAll(fullCommunityCards);

            // Evaluate best 5-card hands
            HandRank playerHandRank = HandEvaluator.evaluateHand(playerFullHand);
            HandRank opponentHandRank = HandEvaluator.evaluateHand(opponentFullHand);

            // Compare hands and track results
            int comparison = playerHandRank.compareTo(opponentHandRank);
            if (comparison > 0) wins++;
            else if (comparison == 0) ties++;
        }

        // Calculate win probability (counting ties as half-wins)
        return (wins + 0.5 * ties) / simulationCount;
    }

    /**
     * Runs a simulation that terminates early when statistical confidence is reached
     */
    public double estimateWithConfidence(double confidenceThreshold) {
        int batchSize = 100;
        int iterations = 0;
        int wins = 0, ties = 0;
        double currentEstimate = 0;
        double previousEstimate = -1;

        List<Card> knownCards = new ArrayList<>(playerCards);
        knownCards.addAll(knownCommunityCards);

        while (iterations < simulationCount &&
               (iterations < 1000 || Math.abs(currentEstimate - previousEstimate) > confidenceThreshold)) {

            previousEstimate = currentEstimate;

            // Run a batch of simulations
            for (int i = 0; i < batchSize && iterations < simulationCount; i++, iterations++) {
                Deck simDeck = new Deck();
                simDeck.removeCards(knownCards);


                List<Card> opponentCards = new ArrayList<>();
                opponentCards.add(simDeck.dealCard());
                opponentCards.add(simDeck.dealCard());

                List<Card> fullCommunityCards = new ArrayList<>(knownCommunityCards);
                while (fullCommunityCards.size() < 5) {
                    fullCommunityCards.add(simDeck.dealCard());
                }

                List<Card> playerFullHand = new ArrayList<>(playerCards);
                playerFullHand.addAll(fullCommunityCards);

                List<Card> opponentFullHand = new ArrayList<>(opponentCards);
                opponentFullHand.addAll(fullCommunityCards);

                HandRank playerHandRank = HandEvaluator.evaluateHand(playerFullHand);
                HandRank opponentHandRank = HandEvaluator.evaluateHand(opponentFullHand);

                int comparison = playerHandRank.compareTo(opponentHandRank);
                if (comparison > 0) wins++;
                else if (comparison == 0) ties++;
            }

            currentEstimate = (wins + 0.5 * ties) / iterations;
        }

        return currentEstimate;
    }

    /**
     * Runs a faster simulation with fewer iterations for quick decisions
     */
    public double quickEstimate() {
        return new MonteCarloSimulator(playerCards, knownCommunityCards, 1000).estimateWinProbability();
    }
}