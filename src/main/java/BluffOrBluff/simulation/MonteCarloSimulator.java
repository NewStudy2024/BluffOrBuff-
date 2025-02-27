package BluffOrBluff.simulation;

import BluffOrBluff.model.*;
import java.util.*;

    public class MonteCarloSimulator {
        private static final int SIMULATIONS = 10000; // Number of Monte Carlo simulations
        private final Deck deck;
        private final List<Card> communityCards;
        private final List<Card> aiHand;

        public MonteCarloSimulator(List<Card> aiHand, List<Card> communityCards) {
            this.deck = new Deck();
            this.communityCards = new ArrayList<>(communityCards);
            this.aiHand = new ArrayList<>(aiHand);
        }

        public double estimateWinProbability() {
            int wins = 0, ties = 0;
            int totalSimulations = SIMULATIONS;

            for (int i = 0; i < totalSimulations; i++) {
                // Reset deck and deal random opponent hand
                Deck simDeck = new Deck();
                simDeck.removeCards(aiHand);
                simDeck.removeCards(communityCards);
                List<Card> opponentHand = Arrays.asList(simDeck.dealCard(), simDeck.dealCard());

                // Complete community cards if not already 5
                List<Card> fullCommunity = new ArrayList<>(communityCards);
                while (fullCommunity.size() < 5) {
                    fullCommunity.add(simDeck.dealCard());
                }

                HandRank aiBestHand = HandEvaluator.evaluateHand(new ArrayList<>(aiHand));
                HandRank opponentBestHand = HandEvaluator.evaluateHand(opponentHand);

                int comparison = aiBestHand.compareTo(opponentBestHand);
                if (comparison > 0) wins++;
                else if (comparison == 0) ties++;
            }

            return (wins + 0.5 * ties) / totalSimulations;
        }
    }
