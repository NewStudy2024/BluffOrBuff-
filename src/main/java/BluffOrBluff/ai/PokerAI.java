package BluffOrBluff.ai;

import BluffOrBluff.model.*;
import BluffOrBluff.logic.RoundStage;
import BluffOrBluff.simulation.MonteCarloSimulator;

import java.util.List;
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

    public BettingAction getAIDecision(HandRank aiHandRank, int currentBet, int pot, RoundStage stage, List<Card> communityCards) {
        // int handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank()); TODO if anomaly revert
        //int handStrength;
        int aiChips = ai.getChips();

        if (aiChips == 0) {
            return BettingAction.CHECK; // No chips left
        }

        // Short-stack scenario
        if (aiChips < currentBet) {
            int handStrength = HandEvaluator.getHandRankValue(aiHandRank.getRank());
            return handleShortStackDecision(handStrength, stage);
        }

        // Pre-flop uses simple strategy
        if (stage == RoundStage.PRE_FLOP) {
            return BettingAction.CALL;
        }

        // Post-flop uses Monte Carlo simulation
        List<Card> aiCards = ai.getHand().getCards();
        double winProbability;

        // Different simulation depths based on difficulty
        if (difficulty == 3) {
            // Expert AI - full simulation
            MonteCarloSimulator simulator = new MonteCarloSimulator(aiCards, communityCards);
            winProbability = simulator.estimateWinProbability();
        } else if (difficulty == 2) {
            // Normal AI - adaptive simulation with confidence threshold
            MonteCarloSimulator simulator = new MonteCarloSimulator(aiCards, communityCards, 3000);
            winProbability = simulator.estimateWithConfidence(0.02);
        } else {
            // Beginner AI - quick estimate
            MonteCarloSimulator simulator = new MonteCarloSimulator(aiCards, communityCards, 1000);
            winProbability = simulator.quickEstimate();
        }

        return makeProbabilityBasedDecision(winProbability, currentBet, pot, stage, aiChips);
    }

    private BettingAction makeProbabilityBasedDecision(double winProbability, int currentBet, int pot, RoundStage stage, int aiChips) {
        // Calculate pot odds for decision making
        double potOdds = currentBet > 0 ? (double) currentBet / (pot + currentBet) : 0;

        if (difficulty == 1) { // Beginner AI - simple decisions
            if (winProbability < 0.3) {
                return currentBet == 0 ? BettingAction.CHECK : BettingAction.FOLD;
            }
            if (winProbability < 0.5) {
                return currentBet == 0 ? BettingAction.CHECK : BettingAction.CALL;
            }
            if (winProbability > 0.7 && random.nextInt(100) < 30) {
                return BettingAction.RAISE;
            }
            return currentBet == 0 ? BettingAction.BET : BettingAction.CALL;
        } else if (difficulty == 2) { // Normal AI - moderate strategy
            if (winProbability < 0.25 && currentBet > 0) {
                return BettingAction.FOLD;
            }
            if (winProbability < 0.45) {
                return currentBet == 0 ? BettingAction.CHECK : BettingAction.CALL;
            }
            if (winProbability < 0.6 && random.nextInt(100) < 20 && stage == RoundStage.RIVER) {
                return currentBet == 0 ? BettingAction.BET : BettingAction.RAISE;
            }
            if (winProbability > 0.6) {
                return currentBet == 0 ? BettingAction.BET : BettingAction.RAISE;
            }
            return currentBet == 0 ? BettingAction.CHECK : BettingAction.CALL;
        } else { // Expert AI - sophisticated strategy
            // Profitable call calculation
            boolean profitableCall = potOdds < winProbability;

            if (winProbability < 0.2 && currentBet > 0) {
                return BettingAction.FOLD;
            }
            if (winProbability < 0.3 && random.nextInt(100) < 15 && pot > 100) {
                return currentBet == 0 ? BettingAction.BET : BettingAction.RAISE; // Occasional bluff
            }
            if (winProbability > 0.8 && random.nextInt(100) < 40 && stage == RoundStage.RIVER) {
                return BettingAction.ALL_IN; // Strong hand, go all-in
            }
            if (winProbability > 0.65) {
                return currentBet == 0 ? BettingAction.BET : BettingAction.RAISE;
            }
            if (profitableCall) {
                return currentBet == 0 ? BettingAction.CHECK : BettingAction.CALL;
            }
            return currentBet == 0 ? BettingAction.CHECK : BettingAction.FOLD;
        }
    }

    // Keep all existing methods below unchanged
    private BettingAction handleShortStackDecision(int handStrength, RoundStage stage) {
        // Existing implementation
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

    // Keep other existing methods (decideOnRaise, decideAllInCall, etc.)
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
        double potOdds = (double) currentBet / (pot + currentBet);

        switch (difficulty) {
            case 1:
                if (handStrength < 6 || potOdds > 0.5) return 2; // Fold
                return 1; // Call
            case 2:
                if (handStrength >= 7 || potOdds < 0.4) return 1;
                return 2;
            case 3:
                if (handStrength >= 5 || potOdds < 0.6) return 1;
                return 2;
            default:
                return 1; // Default to calling
        }
    }
}
 /*   // 🔹 Handles AI betting when it has enough chips to play normally
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
*/