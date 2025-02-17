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
        int aiChips = ai.getChips();

        if (aiChips < currentBet) {
            switch (difficulty) {
                case 1 -> { // 🔴 Beginner AI: Passive
                    if (handStrength >= 8) {
                        return BettingAction.ALL_IN;
                    }
                    return BettingAction.FOLD;
                }
                case 2 -> { // 🟠 Normal AI: Balanced
                    if (handStrength >= 7) {
                        return BettingAction.ALL_IN;
                    }
                    if (stage == RoundStage.RIVER && random.nextInt(100) < 20) {
                        return BettingAction.ALL_IN;
                    }
                    return aiChips > 0 ? BettingAction.CALL : BettingAction.FOLD;
                }
                case 3 -> { // 🟢 Expert AI: Aggressive & Smart
                    if (handStrength >= 6) {
                        return BettingAction.ALL_IN;
                    }
                    if (stage == RoundStage.RIVER && random.nextInt(100) < 40) {
                        return BettingAction.ALL_IN;
                    }
                    return aiChips > 0 ? BettingAction.CALL : BettingAction.FOLD;l
                }
            }
        }

        // Beginner AI: Passive, plays safe
        if (difficulty == 1) {
            if (handStrength < 4 && currentBet > (pot / 3)) {
                return BettingAction.FOLD;
            } else if (handStrength < 6) {
                return BettingAction.CHECK;
            }
            if (handStrength > 5 && aiChips > currentBet * 2) {
                return BettingAction.BET;
            }
            return BettingAction.CALL;
        }

        // Normal AI: Balanced, bluffs sometimes
        if (difficulty == 2) {
            if (handStrength < 3 && currentBet > (pot / 2)) {
                return BettingAction.FOLD;
            }
            if (handStrength > 5 && aiChips > currentBet * 2 && random.nextInt(100) < 20) {
                return BettingAction.BET;
            }
            if (handStrength >= 6 || (random.nextInt(100) < 15 && stage == RoundStage.RIVER)) {
                return BettingAction.RAISE;
            }
            return BettingAction.CALL;
        }

        // Expert AI: Aggressive, bluffs strategically
        if (difficulty == 3) {
            if (handStrength < 3 && currentBet > (pot / 2)) {
                return BettingAction.FOLD;
            }
            if (handStrength > 6 && aiChips > currentBet * 2 && random.nextInt(100) < 30) {
                return BettingAction.BET; // New: Expert AI bets more often
            }
            if (handStrength >= 7 || (random.nextInt(100) < 25 && stage == RoundStage.TURN)) {
                return BettingAction.RAISE;
            }
            if (handStrength >= 8 || (random.nextInt(100) < 30 && stage == RoundStage.RIVER)) {
                return BettingAction.ALL_IN;
            }
            return BettingAction.CALL;
        }

        return BettingAction.CHECK;
    }
}
