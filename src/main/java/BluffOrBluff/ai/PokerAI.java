package BluffOrBluff.ai;

import BluffOrBluff.model.*;
import BluffOrBluff.logic.RoundStage;
import java.util.Random;

public class PokerAI {
    private final Player ai;
    private final int difficulty;
    private final Random random;

    public PokerAI(Player ai, int difficulty) {
        this.ai = ai;
        this.difficulty = difficulty;
        this.random = new Random();
    }

    public BettingAction getAIDecision(HandRank aiHandRank, int currentBet, int pot, RoundStage stage) {
        int handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank());


        if (stage == RoundStage.PRE_FLOP) {
            handStrength = HandEvaluator.getPreFlopHandStrength(ai.getHand().getCards());
//            System.out.println("  - Pre-Flop Hand Strength: " + handStrength); TODO remove later
        } else {
            handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank());
        }

        int aiChips = ai.getChips();
        BettingAction action;

//        System.out.println("\n[DEBUG] AI Decision:");
////        System.out.println("  - Hand Strength: " + handStrength); TODO: temporary
//        System.out.println("  - AI Chips: " + aiChips);
////        System.out.println("  - Current Bet: " + currentBet); TODO: should be moved to round manager
////        System.out.println("  - Pot Size: " + pot); TODO: should be also moved to round manager
//        System.out.println("  - Stage: " + stage);

        // AI cannot fold if it has 0 chips - it must check and go to showdown
        if (aiChips == 0) {
//            System.out.println("  - AI has no chips left → CHECK (forced showdown)"); TODO removrd for clean check
            return BettingAction.CHECK;
        }

        // If AI has fewer chips than the current bet, handle the short-stack scenario
        if (aiChips < currentBet) {
            action = handleShortStackDecision(handStrength, stage);
//            System.out.println("  - AI Short-Stack Decision: " + action); TODO removrd for clean check
            return action;
        }

        // AI has enough chips, use normal betting strategy
        action = handleRegularBetting(handStrength, aiChips, currentBet, pot, stage);
//        System.out.println("  - AI Regular Decision: " + action); TODO removrd for clean check
        return action;
    }

    // 🔹 Handles AI betting when it has fewer chips than the current bet
    private BettingAction handleShortStackDecision(int handStrength, RoundStage stage) {
        switch (difficulty) {
            case 1: // 🔴 Beginner AI: Passive, plays safe
                return (handStrength >= 7) ? BettingAction.ALL_IN : BettingAction.CALL;

            case 2: // 🟠 Normal AI: Balanced, takes moderate risks
                if (handStrength >= 7 || (stage == RoundStage.RIVER && random.nextInt(100) < 30)) {
                    return BettingAction.ALL_IN;
                }
                return BettingAction.CHECK;

            case 3: // 🟢 Expert AI: Aggressive, strategic bluffs
                if (handStrength >= 6 || (stage == RoundStage.RIVER && random.nextInt(100) < 40)) {
                    return BettingAction.ALL_IN;
                }
                return BettingAction.CHECK;
        }
        return BettingAction.CHECK; // Default safety return
    }

    // 🔹 Handles AI betting when it has enough chips to play normally
    private BettingAction handleRegularBetting(int handStrength, int aiChips, int currentBet, int pot, RoundStage stage) {
//        System.out.println("\n[DEBUG] Regular Betting Analysis:");
//        System.out.println("  - AI Chips: " + aiChips);
////        System.out.println("  - Hand Strength: " + handStrength); TODO: temporary
////        System.out.println("  - Current Bet: " + currentBet); TODO: should be moved to round manager
////        System.out.println("  - Pot: " + pot);  TODO: should be also moved to round manager
//        System.out.println("  - Stage: " + stage);

        switch (difficulty) {
            case 1: // 🔴 Beginner AI: Passive, folds weak hands
                if (handStrength < 4) {
//                    System.out.println("  - Weak hand → FOLD");
                    return currentBet == 0 ? BettingAction.CHECK : BettingAction.FOLD; // Check if no bet
                }
                if (handStrength < 6) {
//                    System.out.println("  - Mediocre hand → CHECK");
                    return BettingAction.CHECK;
                }
                if (handStrength > 5 && aiChips > currentBet * 2) {
//                    System.out.println("  - Decent hand → BET");
                    return BettingAction.BET;
                }
                if (handStrength >= 9 && random.nextInt(100) < 15) {
//                    System.out.println("  - Very string hand → RISE");
                    return BettingAction.RAISE;
                }
//                System.out.println("  - Default: CALL");
                return BettingAction.CALL;

            case 2: // 🟠 Normal AI: Balanced
                if (handStrength < 3 || (currentBet > aiChips / 3 && random.nextInt(100) < 50)) {
//                    System.out.println("  - Weak hand OR bet too high → FOLD");
                    return currentBet == 0 ? BettingAction.CHECK : BettingAction.FOLD; // Normal AI checks more
                }
                if (handStrength >= 6 || (random.nextInt(100) < 15 && stage == RoundStage.RIVER)) {
//                    System.out.println("  - Strong hand OR river bluff → RAISE");
                    return BettingAction.RAISE;
                }
                if (random.nextInt(100) < 10) {// Small bluff chance
//                    System.out.println("  - Small bluff → BET");
                    return BettingAction.BET;
                }
//                System.out.println("  - Default: CALL");
                return BettingAction.CALL;

            case 3: // 🟢 Expert AI: Aggressive, bluffs strategically
                if (stage == RoundStage.FLOP && pot < 100 && random.nextInt(100) < 10) {
                    return BettingAction.BET; // Reduce bluffing chance
                }
                if (stage == RoundStage.TURN || stage == RoundStage.RIVER) {
                    if (handStrength < 3 && pot < aiChips / 2) {
                        return BettingAction.FOLD; // Fold if weak and pot is not big
                    }
                }
                if (handStrength < 3 && currentBet > (pot / 2) && random.nextInt(100) < 50) {
//                    System.out.println("  - Trash hand & high bet → FOLD");
                    return BettingAction.FOLD; // Expert AI folds sometimes if hand is trash
                }
                if (handStrength >= 7 || (random.nextInt(100) < 25 && stage == RoundStage.TURN)) {
//                    System.out.println("  - Strong hand OR Turn bluff → RAISE");
                    return BettingAction.RAISE;
                }
                if (handStrength >= 8 || (random.nextInt(100) < 30 && stage == RoundStage.RIVER)) {
//                    System.out.println("  - Very strong hand OR big river bluff → ALL IN");
                    return BettingAction.ALL_IN;
                }
                if (random.nextInt(100) < 20) { // Expert AI bluffs more
//                    System.out.println("  - More aggressive bluff → BET");
                    return BettingAction.BET;
                }
//                System.out.println("  - Default: CALL");
                return BettingAction.CALL;
        }
//        System.out.println("  - Default safety CHECK");
        return BettingAction.CHECK; // Default safety return
    }
}
