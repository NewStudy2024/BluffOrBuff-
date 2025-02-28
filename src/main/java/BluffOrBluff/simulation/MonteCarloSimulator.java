package BluffOrBluff.simulation;

import BluffOrBluff.model.*;
import java.util.*;

public class MonteCarloSimulator {
    private static final int DEFAULT_SIMULATIONS = 10000;
    private final List<Card> playerCards;         // Player's hole cards
    private final List<Card> knownCommunityCards; // Known community cards
    private final int simulationCount;

    /**
     * Creates a Monte Carlo simulator with default simulation count
     */
    public MonteCarloSimulator(List<Card> playerCards, List<Card> communityCards) {
        this(playerCards, communityCards, DEFAULT_SIMULATIONS);
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
     * Estimates the probability of winning based on current cards
     * @return Probability between 0.0 (certain loss) and 1.0 (certain win)
     */
    public double estimateWinProbability() {
        int wins = 0, ties = 0;

        // Create a list of all known cards in play
        List<Card> knownCards = new ArrayList<>();
        knownCards.addAll(playerCards);
        knownCards.addAll(knownCommunityCards);

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

            // Create combined hands with community cards
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
     * Runs a faster simulation with fewer iterations for early game stages
     */
    public double quickEstimate() {
        return new MonteCarloSimulator(playerCards, knownCommunityCards, 1000).estimateWinProbability();
    }
}