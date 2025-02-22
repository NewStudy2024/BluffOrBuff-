/**
 *  .--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--.
 * / .. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \
 * \ \/\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ \/ /
 *  \/ /`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'\/ /
 *  / /\                                                                                                            / /\
 * / /\ \                                                                                                          / /\ \
 * \ \/ /  __________.__          _____  _____  ________          __________        _____  _____     ___  ___      \ \/ /
 *  \/ /   \______   \  |  __ ___/ ____\/ ____\ \_____  \_______  \______   \__ ___/ ____\/ ____\   /  /  \  \      \/ /
 *  / /\    |    |  _/  | |  |  \   __\\   __\   /   |   \_  __ \  |    |  _/  |  \   __\\   __\   /  /    \  \     / /\
 * / /\ \   |    |   \  |_|  |  /|  |   |  |    /    |    \  | \/  |    |   \  |  /|  |   |  |    (  (      )  )   / /\ \
 * \ \/ /   |______  /____/____/ |__|   |__|    \_______  /__|     |______  /____/ |__|   |__|     \  \    /  /    \ \/ /
 *  \/ /           \/                                   \/                \/                        \__\  /__/      \/ /
 *  / /\     __                                 .__           .__       .__/\                                       / /\
 * / /\ \  _/  |_  ____ ___  ________    ______ |  |__   ____ |  |    __| _)/___   _____                           / /\ \
 * \ \/ /  \   __\/ __ \\  \/  /\__  \  /  ___/ |  |  \ /  _ \|  |   / __ |/ __ \ /     \                          \ \/ /
 *  \/ /    |  | \  ___/ >    <  / __ \_\___ \  |   Y  (  <_> )  |__/ /_/ \  ___/|  Y Y  \                          \/ /
 *  / /\    |__|  \___  >__/\_ \(____  /____  > |___|  /\____/|____/\____ |\___  >__|_|  /                          / /\
 * / /\ \             \/      \/     \/     \/       \/                  \/    \/      \/                          / /\ \
 * \ \/ /                                                                                                          \ \/ /
 *  \/ /                                                                                                            \/ /
 *  / /\.--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--..--./ /\
 * / /\ \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \.. \/\ \
 * \ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `'\ `' /
 *  `--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'`--'
 */


package BluffOrBluff;

import BluffOrBluff.exception.GameException;
import BluffOrBluff.model.*;
import BluffOrBluff.logic.RoundStage;
import BluffOrBluff.ai.PokerAI;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws GameException {
        // Create AI player with starting chips
        Player aiPlayer = new Player("AI", 100);
        PokerAI pokerAI = new PokerAI(aiPlayer, 3); // Normal AI Difficulty

        // 🃏 Deal hole cards to AI
        aiPlayer.receiveCard(new Card(Card.Rank.ACE, Card.Suit.HEARTS));
        aiPlayer.receiveCard(new Card(Card.Rank.KING, Card.Suit.SPADES));

        // ✅ Pre-Flop: Use specialized method for pre-flop hand strength
        int preFlopStrength = HandEvaluator.getPreFlopHandStrength(aiPlayer.getHand().getCards());
        System.out.println("Pre-Flop Decision: " + pokerAI.getAIDecision(
                new HandRank("PreFlop", List.of(preFlopStrength)),
                10, 50, RoundStage.PRE_FLOP));

        // 🌟 Community Cards for Flop, Turn, and River
        List<Card> communityCards = new ArrayList<>();
        communityCards.add(new Card(Card.Rank.JACK, Card.Suit.DIAMONDS));
        communityCards.add(new Card(Card.Rank.TEN, Card.Suit.CLUBS));
        communityCards.add(new Card(Card.Rank.FOUR, Card.Suit.SPADES));

        // ✅ Flop: AI now has hole cards + 3 community cards
        HandRank flopHandRank = HandEvaluator.evaluateHand(aiPlayer.getFullHand(communityCards));
        System.out.println("Flop Decision: " + pokerAI.getAIDecision(flopHandRank, 20, 100, RoundStage.FLOP));

        // ✅ Turn: Add one more card
        communityCards.add(new Card(Card.Rank.SEVEN, Card.Suit.HEARTS));
        HandRank turnHandRank = HandEvaluator.evaluateHand(aiPlayer.getFullHand(communityCards));
        System.out.println("Turn Decision: " + pokerAI.getAIDecision(turnHandRank, 30, 150, RoundStage.TURN));

        // ✅ River: Add final community card
        communityCards.add(new Card(Card.Rank.TWO, Card.Suit.SPADES));
        HandRank riverHandRank = HandEvaluator.evaluateHand(aiPlayer.getFullHand(communityCards));
        System.out.println("River Decision: " + pokerAI.getAIDecision(riverHandRank, 50, 200, RoundStage.RIVER));

        // Show AI's hand
        System.out.println("AI hand: " + aiPlayer.getHand());
        System.out.println("Community Cards: " + communityCards);
    }
}












