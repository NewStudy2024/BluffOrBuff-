package BluffOrBluff.logic;

import BluffOrBluff.model.*;
import BluffOrBluff.exception.GameException;
import BluffOrBluff.util.InputHandler;
import BluffOrBluff.ai.PokerAI;

import java.util.ArrayList;
import java.util.List;

public class RoundManager {
    private final PokerAI pokerAI;
    private Deck deck;
    private final Player human;
    private final Player ai;
    private final List<Card> communityCards = new ArrayList<>();
    private int pot;
    private int difficulty;
    private RoundStage currentStage;
    private int roundCounter = 0;
    private boolean playerAllIn;
    private boolean aiAllIn;

    public RoundManager(Deck deck, Player human, Player ai, int difficulty) {
        this.deck = deck;
        this.human = human;
        this.ai = ai;
        this.pot = 0;
        this.difficulty = difficulty;
        this.currentStage = RoundStage.PRE_FLOP;
        this.pokerAI = new PokerAI(ai, difficulty);
        this.playerAllIn = false;
        this.aiAllIn = false;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void playRound() {
        System.out.println("\n--- New Round ---");
        resetRound();
        try {
            dealHoleCards();
        } catch (GameException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        // AI no longer starts first. Human always plays first.
        //boolean aiGoesFirst = (roundCounter % 2 == 0);

        currentStage = RoundStage.PRE_FLOP;
        if (!bettingPhase()) return;

        currentStage = RoundStage.FLOP;
        dealCommunityCards(3);
        if (!bettingPhase()) return;

        currentStage = RoundStage.TURN;
        dealCommunityCards(1);
        if (!bettingPhase()) return;

        currentStage = RoundStage.RIVER;
        dealCommunityCards(1);
        if (!bettingPhase()) return;

        currentStage = RoundStage.SHOWDOWN;
        determineRoundWinner();

        roundCounter++;  // Move to the next round after a full hand is completed

    }

    private void resetRound() {
        this.deck = new Deck();
        deck.shuffle();
        human.resetHand();
        ai.resetHand();
        communityCards.clear();
        pot = 0;
        currentStage = RoundStage.PRE_FLOP;
    }

    private void dealHoleCards() throws GameException {
        try {
            human.receiveCard(deck.dealCard());
            human.receiveCard(deck.dealCard());
            ai.receiveCard(deck.dealCard());
            ai.receiveCard(deck.dealCard());
        } catch (GameException e) {
            System.out.println("Error dealing cards: " + e.getMessage());
        }
        human.showCards();
        System.out.println("AI's hole cards: " + ai.getHand());
    }

    private void dealCommunityCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            communityCards.add(deck.dealCard());
        }
        System.out.println("Community Cards: " + communityCards);
    }

    private void printChipCounts() {
        System.out.println("\nChip Counts:");
        System.out.println(STR."\{human.getName()} Chips: \{human.getChips()}");
        System.out.println(STR."AI Chips: \{ai.getChips()}");
    }

    private void printPot(int currentBet) {
        System.out.println("Total pot: " + pot + " chips (Current Bet: " + currentBet + ")");

    }

    private void printLines(){
        System.out.println("\n".repeat(3));
    }

    private int processBettingTurn(Player player, boolean isAI, int currentBet) {
        BettingAction action;

        if (isAI) {
            HandRank aiHandRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));
            action = pokerAI.getAIDecision(aiHandRank, currentBet, pot, currentStage, communityCards);
        } else {
            action = getPlayerDecision(currentBet, player.getChips());
            System.out.println(player.getName() + " chooses: " + action);
        }

        switch (action) {
            case FOLD:
                System.out.println(player.getName() + " folded.");
            return -1;

            case ALL_IN:
            int allInAmount = Math.min(player.getChips(), currentBet);
                System.out.println(player.getName() + " goes ALL-IN with " + allInAmount + " chips!");
                if (isAI) aiAllIn = true;
                else playerAllIn = true;
                player.moneyLost();
                pot += allInAmount;
                return allInAmount;

            case RAISE:
                int raiseAmount;
                if (isAI) {
                    raiseAmount = Math.min(50 + (difficulty * 25), player.getChips());
                    if (currentBet > 0) raiseAmount = Math.min(currentBet + raiseAmount, player.getChips());
                } else {
                    raiseAmount = getRaiseAmount(player.getChips(), currentBet);
                }

                player.addChips(-raiseAmount);
                pot += raiseAmount;
                System.out.println(player.getName() + " raises by " + raiseAmount + " chips!");
                return raiseAmount;

            case CALL:
                int callAmount =Math.min(currentBet, player.getChips());
                if (callAmount > player.getChips()) callAmount = player.getChips();
                player.addChips(-callAmount);
                pot += callAmount;
                System.out.println(player.getName() + " calls the bet of " + callAmount + " chips.");
                return currentBet; // Returns the same bet amount instead of overwriting it**

            case CHECK:
                System.out.println(player.getName() + " checks.");
                return currentBet; // No change in bet

            default:
                System.out.println("Invalid action by " + player.getName() + ".");
                return currentBet; // Keep the same bet if the action is invalid
        }
    }

    private BettingAction getPlayerDecision(int currentBet, int playerChips) {
        human.showCards();

        if (currentBet == 0) { // No active bet → Allow Check, Raise, All-In
            System.out.println("[1] Check  [2] Bet  [3] All-In  [4] Fold");
            int choice = InputHandler.getValidInt(1, 4);
            return switch (choice) {
                case 1 -> BettingAction.CHECK;
                case 2 -> BettingAction.RAISE;
                case 3 -> BettingAction.ALL_IN;
                case 4 -> BettingAction.FOLD;
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            };
        }

        else if (playerAllIn || aiAllIn) {
            System.out.println("[1] Call  [2] Fold");
            int choice = InputHandler.getValidInt(1, 2);
            return switch (choice) {
                case 1 -> BettingAction.CALL;
                case 2 -> BettingAction.FOLD;
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            };
        }

        else { // Active bet → Player must Call, Raise, Fold, or All-In
            System.out.println("[1] Call  [2] Raise  [3] All-In  [4] Fold");
            int choice = InputHandler.getValidInt(1, 4);
            return switch (choice) {
                case 1 -> BettingAction.CALL;
                case 2 -> BettingAction.RAISE;
                case 3 -> BettingAction.ALL_IN;
                case 4 -> BettingAction.FOLD;
                default -> throw new IllegalStateException("Unexpected value: " + choice);
            };
        }
    }

    private boolean bettingPhase() {
        System.out.println("\n--- " + currentStage + " Betting Phase ---");
        printChipCounts();
        human.showCards();

        int currentBet = 0;
        System.out.println("\n[DEBUG] Human acts first");

        // **Human makes the first move**
        currentBet = processBettingTurn(human, false, currentBet);
        if (currentBet == -1) return handleFold(ai); // Human folded
        if (playerAllIn) return askForAllInDecision(ai, true, currentBet); // If human is all-in, AI must react

        // **AI responds**
        int aiBet = processBettingTurn(ai, true, currentBet);
        if (aiBet == -1) return handleFold(human); // AI folded
        if (aiAllIn) return askForAllInDecision(human, false, aiBet); // If AI goes all-in, human must react

        // **Loop continues only if AI raises**
        while (aiBet > currentBet) {
            currentBet = processBettingTurn(human, false, aiBet);
            if (currentBet == -1) return handleFold(ai);
            if (playerAllIn) return askForAllInDecision(ai, true, currentBet);

            aiBet = processBettingTurn(ai, true, currentBet);
            if (aiBet == -1) return handleFold(human);
            if (aiAllIn) return askForAllInDecision(human, false, aiBet);
            if (aiBet == currentBet) break;
        }

        return true;
    }

    private boolean askForAllInDecision(Player opponent, boolean isOpponentAI, int currentBet) {
        System.out.println(opponent.getName() + ", your opponent is ALL-IN!");
        System.out.println("[1] Call  [2] Fold");

        int choice;
        if (isOpponentAI) {
            HandRank aiHandRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));
            choice = pokerAI.decideOnRaise(currentBet, pot, aiHandRank);
        } else {
            choice = InputHandler.getValidInt(1, 2);
        }

        if (choice == 2) { // Opponent folds
            System.out.println(opponent.getName() + " folded. The All-In player wins the round!");
            if (playerAllIn) human.addChips(pot);
            else ai.addChips(pot);
            return false;
        }

        System.out.println(opponent.getName() + " calls!");
        dealRemainingCommunityCards();
        determineRoundWinner();
        return false;
    }

    private void dealRemainingCommunityCards() {
        int cardsNeeded = 5 - communityCards.size();
        if (cardsNeeded > 0) {
            dealCommunityCards(cardsNeeded);
        }
    }

    private boolean handleAllInScenario() {
        System.out.println("A player is All-In! Moving directly to Showdown.");
        int communityCardsNum = communityCards.size();
        if (communityCardsNum != 5) {
            dealCommunityCards(5-communityCardsNum);
        }
        determineRoundWinner();
        return false;
    }

    private int getRaiseAmount(int playerChips, int currentBet) {
        int minRaise = currentBet == 0 ? 50 : currentBet + 50;
        System.out.println("Enter raise amount (Minimum: " + minRaise + " chips, Maximum: " + playerChips + " chips):");
        return InputHandler.getValidInt(minRaise, playerChips);
    }

    private boolean handleFold(Player winner) {
        System.out.println(winner.getName() + " wins the round.");
        winner.addChips(pot);
        return false;
    }

    private void determineRoundWinner() {
        printLines();
        System.out.println("\n--- SHOWDOWN ---");
        System.out.println("AI's hole cards: " + ai.getHand());

        HandRank humanHandRank = HandEvaluator.evaluateHand(human.getFullHand(communityCards));
        HandRank aiHandRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));

        System.out.println(human.getName() + "'s best hand: " + humanHandRank);
        System.out.println("AI's best hand: " + aiHandRank);

        if (humanHandRank.compareTo(aiHandRank) > 0) {
            System.out.println(human.getName() + " wins with " + humanHandRank);
            human.addChips(pot);
        } else if (humanHandRank.compareTo(aiHandRank) < 0) {
            System.out.println("AI wins with " + aiHandRank);
            ai.addChips(pot);
        } else {
            System.out.println("It's a tie! Both have " + humanHandRank);
            human.addChips(pot / 2);
            ai.addChips(pot / 2);
        }

        // Ensure chip amounts are correct after All-In scenarios
        if (playerAllIn && aiHandRank.compareTo(humanHandRank) > 0) {
            human.moneyLost();
        } else if (aiAllIn && humanHandRank.compareTo(aiHandRank) > 0) {
            ai.moneyLost();
        }

        System.out.println("AI chips: " + ai.getChips()); //TODO: do this a bit more elegantly
        System.out.println(human.getName() + " chips: " + human.getChips());
    }
}
