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
        // int handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank()); TODO if anomaly revert
        int handStrength;
        int aiChips = ai.getChips();
        BettingAction action;

        if (stage == RoundStage.PRE_FLOP) {
            return BettingAction.CALL;
        } else {
            handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank());
        }

        //        System.out.println("\n[DEBUG] AI Decision:");
        //        System.out.println("  - Hand Strength: " + handStrength); TODO: temporary
        ////        System.out.println("  - AI Chips: " + aiChips);
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

        // AI has enough chips normal betting
        action = handleRegularBetting(handStrength, aiChips, currentBet, pot, stage);
//        System.out.println("  - AI Regular Decision: " + action); TODO removrd for clean check
        return action;
    }

    public int decideOnRaise(int currentBet, int pot, HandRank aiHandRank) {
        int handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank());
        int aiChips = ai.getChips();

        if (handStrength < 3 && currentBet > aiChips / 3) {
        int foldChance = difficulty == 1 ? 10 : (difficulty == 2 ? 30 : 50);
        if (random.nextInt(100) < foldChance) return currentBet; // AI calls more often
        return -1; // Otherwise, AI folds (only sometimes)
        }


        // AI Calls if already committed or if pot odds are favorable
        double potOdds = (double) currentBet / (pot + currentBet);
        double aiCallChance = (handStrength * 0.15) + (1 - potOdds) + (difficulty * 0.05);
        if (random.nextDouble() < aiCallChance) {
            return currentBet;
        }

        if (currentBet == 0) { // Check
            return 0;
        }

        // raise only if it has a strong hand and hasn't matched the bet
        if (handStrength >= 6 && currentBet < aiChips / 2) {
            int raiseAmount = Math.min(currentBet + 50, aiChips); // Ensures AI doesn't overbet
            return raiseAmount;
        }

        return currentBet;
    }


    public int decideAllInCall(int currentBet, int pot, HandRank aiHandRank, int difficulty) {
        int handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank());

        // Consider Pot Odds for decision
        double potOdds = (double) currentBet / (pot + currentBet);

        switch (difficulty) {
            case 1:
                if (handStrength < 6 || potOdds > 0.5) return 2; // Fold if hand is weak or risk is high
                return 1; // Call if hand is decent

            case 2:
                if (handStrength >= 7 || potOdds < 0.4) return 1; // Call with good hand or low risk
                return 2; // Otherwise fold

            case 3:
                if (handStrength >= 5 || potOdds < 0.6) return 1; // More willing to call
                return 2; // Otherwise, fold

            default:
                return 1; // Default to calling
        }
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
                if (handStrength < 2) {
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
                if (random.nextInt(100) < 30) {// Small bluff chance
//                    System.out.println("  - Small bluff → BET");
                    return BettingAction.BET;
                }
                if (random.nextInt(100) < 40 || (stage == RoundStage.RIVER && random.nextInt(100) < 50)) {
//                    System.out.println("  - Small bluff → BET");
                    return BettingAction.BET; // AI raises 40% of the time (50% on river)
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
                if (random.nextInt(100) < 40 || (stage == RoundStage.RIVER && random.nextInt(100) < 50)) {
//                    System.out.println("  - More aggressive bluff → BET")?
                    return BettingAction.BET; // **🔹 AI now bluffs 40% (50% on River)**
                }
//                System.out.println("  - Default: CALL");
                return BettingAction.CALL;
        }
//        System.out.println("  - Default safety CHECK");
        return BettingAction.CHECK; // Default safety return
    }
}
