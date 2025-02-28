package BluffOrBluff.test;

import BluffOrBluff.ai.PokerAI;
import BluffOrBluff.model.*;
import BluffOrBluff.logic.RoundStage;
import BluffOrBluff.simulation.MonteCarloSimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIDecisionTest {
    public static void main(String[] args) {
        testMonteCarloSimulation();
        testAIDecision();
    }

    private static void testMonteCarloSimulation() {
        System.out.println("=== Testing Monte Carlo Simulation ===");

        // Test with pocket aces on K♥ Q♥ J♦ board
        List<Card> playerCards = new ArrayList<>();
        playerCards.add(new Card(Card.Rank.ACE, Card.Suit.SPADES));
        playerCards.add(new Card(Card.Rank.ACE, Card.Suit.DIAMONDS));

        List<Card> communityCards = new ArrayList<>();
        communityCards.add(new Card(Card.Rank.KING, Card.Suit.HEARTS));
        communityCards.add(new Card(Card.Rank.QUEEN, Card.Suit.HEARTS));
        communityCards.add(new Card(Card.Rank.JACK, Card.Suit.DIAMONDS));

        MonteCarloSimulator simulator = new MonteCarloSimulator(playerCards, communityCards);
        double winProbability = simulator.estimateWinProbability();

        System.out.println("Pocket Aces with K♥ Q♥ J♦ board");
        System.out.println("Win probability: " + String.format("%.2f", winProbability));
    }

    private static void testAIDecision() {
        System.out.println("\n=== Testing AI Decision Making ===\n");

        // Create test player for AI
        Player aiPlayer = new Player("TestAI", 1000);

        try {
            // Give AI strong cards - pocket aces
            aiPlayer.receiveCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
            aiPlayer.receiveCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));

            // Community cards
            List<Card> communityCards = new ArrayList<>();
            communityCards.add(new Card(Card.Rank.KING, Card.Suit.HEARTS));
            communityCards.add(new Card(Card.Rank.QUEEN, Card.Suit.HEARTS));
            communityCards.add(new Card(Card.Rank.JACK, Card.Suit.DIAMONDS));

            // Test different scenarios for different difficulty levels
            testScenario(new PokerAI(aiPlayer, 1), HandEvaluator.evaluateHand(aiPlayer.getFullHand(communityCards)),
                    0, 100, RoundStage.FLOP, communityCards, "No bet on FLOP");

            // Add turn card
            communityCards.add(new Card(Card.Rank.TEN, Card.Suit.SPADES));
            testScenario(new PokerAI(aiPlayer, 2), HandEvaluator.evaluateHand(aiPlayer.getFullHand(communityCards)),
                    200, 400, RoundStage.TURN, communityCards, "Medium bet on TURN");

            // Add river card
            communityCards.add(new Card(Card.Rank.TWO, Card.Suit.CLUBS));
            testScenario(new PokerAI(aiPlayer, 3), HandEvaluator.evaluateHand(aiPlayer.getFullHand(communityCards)),
                    500, 800, RoundStage.RIVER, communityCards, "Large bet on RIVER");

        } catch (Exception e) {
            System.out.println("Error in test setup: " + e.getMessage());
        }
    }

    private static void testScenario(PokerAI ai, HandRank aiHandRank,
                                     int currentBet, int pot, RoundStage stage,
                                     List<Card> communityCards, String description) {
        System.out.println("Testing " + description);
        BettingAction decision = ai.getAIDecision(aiHandRank, currentBet, pot, stage, communityCards);
        System.out.println("AI Decision: " + decision);
    }
}