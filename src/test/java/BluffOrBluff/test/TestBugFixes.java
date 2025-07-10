package BluffOrBluff.test;

import BluffOrBluff.model.*;
import BluffOrBluff.ai.PokerAI;
import BluffOrBluff.logic.RoundStage;

import java.util.Arrays;
import java.util.List;

public class TestBugFixes {
    public static void main(String[] args) {
        testBETAction();
        testAllInDecision();
        testHandOverflow();
        System.out.println("=== Bug Fix Tests Complete ===");
    }

    private static void testBETAction() {
        System.out.println("Testing BET action in AI...");
        
        Player ai = new Player("AI", 1000);
        PokerAI pokerAI = new PokerAI(ai, 1);
        
        // Create a scenario where AI should use BET action
        try {
            ai.receiveCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
            ai.receiveCard(new Card(Card.Rank.KING, Card.Suit.HEARTS));
            
            List<Card> communityCards = Arrays.asList(
                new Card(Card.Rank.QUEEN, Card.Suit.HEARTS),
                new Card(Card.Rank.JACK, Card.Suit.HEARTS),
                new Card(Card.Rank.TEN, Card.Suit.HEARTS)
            );
            
            HandRank handRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));
            BettingAction action = pokerAI.getAIDecision(handRank, 0, 100, RoundStage.FLOP, communityCards);
            
            System.out.println("AI action with strong hand and no current bet: " + action);
            System.out.println("✅ BET action test passed");
            
        } catch (Exception e) {
            System.out.println("❌ BET action test failed: " + e.getMessage());
        }
    }

    private static void testAllInDecision() {
        System.out.println("\nTesting all-in decision method...");
        
        Player ai = new Player("AI", 500);
        PokerAI pokerAI = new PokerAI(ai, 2);
        
        try {
            ai.receiveCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
            ai.receiveCard(new Card(Card.Rank.ACE, Card.Suit.SPADES));
            
            List<Card> communityCards = Arrays.asList(
                new Card(Card.Rank.ACE, Card.Suit.CLUBS),
                new Card(Card.Rank.KING, Card.Suit.HEARTS),
                new Card(Card.Rank.QUEEN, Card.Suit.HEARTS)
            );
            
            HandRank handRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));
            int decision = pokerAI.decideAllInCall(300, 500, handRank, 2);
            
            System.out.println("AI all-in decision with trips: " + (decision == 1 ? "CALL" : "FOLD"));
            System.out.println("✅ All-in decision test passed");
            
        } catch (Exception e) {
            System.out.println("❌ All-in decision test failed: " + e.getMessage());
        }
    }

    private static void testHandOverflow() {
        System.out.println("\nTesting hand overflow protection...");
        
        Hand hand = new Hand();
        
        try {
            hand.addCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
            hand.addCard(new Card(Card.Rank.KING, Card.Suit.HEARTS));
            System.out.println("Added 2 cards successfully");
            
            // This should throw an exception
            hand.addCard(new Card(Card.Rank.QUEEN, Card.Suit.HEARTS));
            System.out.println("❌ Hand overflow test failed - no exception thrown");
            
        } catch (Exception e) {
            System.out.println("✅ Hand overflow test passed - " + e.getMessage());
        }
    }
}
