package BluffOrBluff.test;

import BluffOrBluff.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HandEvaluatorTest {

    @Test
    public void testRoyalFlush() {
        List<Card> royalFlush = List.of(
                new Card(Card.Rank.TEN, Card.Suit.HEARTS),
                new Card(Card.Rank.JACK, Card.Suit.HEARTS),
                new Card(Card.Rank.QUEEN, Card.Suit.HEARTS),
                new Card(Card.Rank.KING, Card.Suit.HEARTS),
                new Card(Card.Rank.ACE, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(royalFlush);
        assertEquals("Royal Flush", result.getRank());
    }

    @Test
    public void testStraightFlush() {
        List<Card> straightFlush = List.of(
                new Card(Card.Rank.SIX, Card.Suit.CLUBS),
                new Card(Card.Rank.SEVEN, Card.Suit.CLUBS),
                new Card(Card.Rank.EIGHT, Card.Suit.CLUBS),
                new Card(Card.Rank.NINE, Card.Suit.CLUBS),
                new Card(Card.Rank.TEN, Card.Suit.CLUBS)
        );
        HandRank result = HandEvaluator.evaluateHand(straightFlush);
        assertEquals("Straight Flush", result.getRank());
    }

    @Test
    public void testFourOfAKind() {
        List<Card> fourOfAKind = List.of(
                new Card(Card.Rank.JACK, Card.Suit.DIAMONDS),
                new Card(Card.Rank.JACK, Card.Suit.CLUBS),
                new Card(Card.Rank.JACK, Card.Suit.HEARTS),
                new Card(Card.Rank.JACK, Card.Suit.SPADES),
                new Card(Card.Rank.TWO, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(fourOfAKind);
        assertEquals("Four of a Kind", result.getRank());
    }

    @Test
    public void testFullHouse() {
        List<Card> fullHouse = List.of(
                new Card(Card.Rank.THREE, Card.Suit.DIAMONDS),
                new Card(Card.Rank.THREE, Card.Suit.CLUBS),
                new Card(Card.Rank.THREE, Card.Suit.HEARTS),
                new Card(Card.Rank.KING, Card.Suit.SPADES),
                new Card(Card.Rank.KING, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(fullHouse);
        assertEquals("Full House", result.getRank());
    }

    @Test
    public void testStraightAceLow() {
        List<Card> straightAceLow = List.of(
                new Card(Card.Rank.ACE, Card.Suit.DIAMONDS),
                new Card(Card.Rank.TWO, Card.Suit.CLUBS),
                new Card(Card.Rank.THREE, Card.Suit.HEARTS),
                new Card(Card.Rank.FOUR, Card.Suit.SPADES),
                new Card(Card.Rank.FIVE, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(straightAceLow);
        assertEquals("Straight", result.getRank());
    }

    @Test
    public void testFlush() {
        List<Card> flush = List.of(
                new Card(Card.Rank.TWO, Card.Suit.HEARTS),
                new Card(Card.Rank.FIVE, Card.Suit.HEARTS),
                new Card(Card.Rank.NINE, Card.Suit.HEARTS),
                new Card(Card.Rank.QUEEN, Card.Suit.HEARTS),
                new Card(Card.Rank.KING, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(flush);
        assertEquals("Flush", result.getRank());
    }

    @Test
    public void testStraight() {
        List<Card> straight = List.of(
                new Card(Card.Rank.FIVE, Card.Suit.DIAMONDS),
                new Card(Card.Rank.SIX, Card.Suit.CLUBS),
                new Card(Card.Rank.SEVEN, Card.Suit.HEARTS),
                new Card(Card.Rank.EIGHT, Card.Suit.SPADES),
                new Card(Card.Rank.NINE, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(straight);
        assertEquals("Straight", result.getRank());
    }

    @Test
    public void testTwoPair() {
        List<Card> twoPair = List.of(
                new Card(Card.Rank.SIX, Card.Suit.DIAMONDS),
                new Card(Card.Rank.SIX, Card.Suit.CLUBS),
                new Card(Card.Rank.NINE, Card.Suit.HEARTS),
                new Card(Card.Rank.NINE, Card.Suit.SPADES),
                new Card(Card.Rank.ACE, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(twoPair);
        assertEquals("Two Pair", result.getRank());
    }

    @Test
    public void testOnePair() {
        List<Card> onePair = List.of(
                new Card(Card.Rank.FIVE, Card.Suit.DIAMONDS),
                new Card(Card.Rank.FIVE, Card.Suit.CLUBS),
                new Card(Card.Rank.NINE, Card.Suit.HEARTS),
                new Card(Card.Rank.KING, Card.Suit.SPADES),
                new Card(Card.Rank.ACE, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(onePair);
        assertEquals("One Pair", result.getRank());
    }

    @Test
    public void testHighCard() {
        List<Card> highCard = List.of(
                new Card(Card.Rank.TWO, Card.Suit.DIAMONDS),
                new Card(Card.Rank.FIVE, Card.Suit.CLUBS),
                new Card(Card.Rank.EIGHT, Card.Suit.HEARTS),
                new Card(Card.Rank.TEN, Card.Suit.SPADES),
                new Card(Card.Rank.KING, Card.Suit.HEARTS)
        );
        HandRank result = HandEvaluator.evaluateHand(highCard);
        assertEquals("High Card", result.getRank());
    }

    @Test
    public void testCompareFourOfAKindVsFullHouse() {
        HandRank fourOfAKind = new HandRank("Four of a Kind", List.of(8, 5)); // Four Eights, kicker 5
        HandRank fullHouse = new HandRank("Full House", List.of(7, 5)); // Sevens full of Fives
        assertTrue(fourOfAKind.compareTo(fullHouse) > 0); // Four of a Kind should win
    }

    @Test
    public void testCompareStraightFlushVsFlush() {
        HandRank straightFlush = new HandRank("Straight Flush", List.of(10, 9, 8, 7, 6));
        HandRank flush = new HandRank("Flush", List.of(14, 12, 10, 7, 4)); // A-Q-T-7-4 suited
        assertTrue(straightFlush.compareTo(flush) > 0); // Straight Flush should win
    }

    @Test
    public void testCompareFullHouseDifferentRanks() {
        HandRank fullHouse1 = new HandRank("Full House", List.of(10, 3)); // Tens full of Threes
        HandRank fullHouse2 = new HandRank("Full House", List.of(9, 3)); // Nines full of Threes
        assertTrue(fullHouse1.compareTo(fullHouse2) > 0); // Higher three-of-a-kind wins
    }

    @Test
    public void testCompareFlushVsStraight() {
        HandRank flush = new HandRank("Flush", List.of(13, 11, 9, 7, 4)); // K-J-9-7-4 suited
        HandRank straight = new HandRank("Straight", List.of(10, 9, 8, 7, 6));
        assertTrue(flush.compareTo(straight) > 0); // Flush should win over Straight
    }

    @Test
    public void testCompareTwoPairsDifferentKickers() {
        HandRank twoPair1 = new HandRank("Two Pair", List.of(10, 8, 5)); // 10s & 8s, kicker 5
        HandRank twoPair2 = new HandRank("Two Pair", List.of(10, 8, 4)); // 10s & 8s, kicker 4
        assertTrue(twoPair1.compareTo(twoPair2) > 0); // Higher kicker wins
    }

    @Test
    public void testCompareOnePairDifferentKickers() {
        HandRank pair1 = new HandRank("One Pair", List.of(10, 8, 5)); // Pair of 10s with 8 kicker
        HandRank pair2 = new HandRank("One Pair", List.of(10, 7, 5)); // Pair of 10s with 7 kicker
        assertTrue(pair1.compareTo(pair2) > 0); // Higher kicker wins
    }

    @Test
    public void testCompareSameHighCardDifferentKickers() {
        HandRank highCard1 = new HandRank("High Card", List.of(14, 12, 9, 7, 5)); // A-Q-9-7-5
        HandRank highCard2 = new HandRank("High Card", List.of(14, 12, 8, 7, 5)); // A-Q-8-7-5
        assertTrue(highCard1.compareTo(highCard2) > 0); // Q-9 vs Q-8, 9 wins
    }

    @Test
    public void testCompareIdenticalHands() {
        HandRank identical1 = new HandRank("Flush", List.of(14, 12, 9, 7, 5)); // A-Q-9-7-5 suited
        HandRank identical2 = new HandRank("Flush", List.of(14, 12, 9, 7, 5)); // Same hand
        assertEquals(0, identical1.compareTo(identical2)); // Exact match, should be tie
    }

    @Test
    public void testCompareKickerForTrips() {
        HandRank trips1 = new HandRank("Three of a Kind", List.of(9, 6, 4)); // Three Nines, kicker 6
        HandRank trips2 = new HandRank("Three of a Kind", List.of(9, 5, 4)); // Three Nines, kicker 5
        assertTrue(trips1.compareTo(trips2) > 0); // Higher kicker should win
    }

    @Test
    public void testComparePairVsPair() {
        HandRank pair1 = new HandRank("One Pair", List.of(11, 9, 6, 3)); // Jacks with 9-6-3 kickers
        HandRank pair2 = new HandRank("One Pair", List.of(11, 9, 6, 2)); // Jacks with 9-6-2 kickers
        assertTrue(pair1.compareTo(pair2) > 0); // The third kicker (3 vs. 2) decides the winner
    }

    @Test
    public void testCompareWeakerFlushVsStrongerFlush() {
        HandRank flush1 = new HandRank("Flush", List.of(13, 11, 9, 7, 4)); // K-J-9-7-4
        HandRank flush2 = new HandRank("Flush", List.of(14, 12, 10, 7, 4)); // A-Q-T-7-4
        assertTrue(flush2.compareTo(flush1) > 0); // Ace-high flush wins
    }
}
