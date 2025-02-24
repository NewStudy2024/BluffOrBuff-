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

        boolean aiGoesFirst = (roundCounter % 2 == 0);

        currentStage = RoundStage.PRE_FLOP;
        if (!bettingPhase(aiGoesFirst)) return;

        currentStage = RoundStage.FLOP;
        dealCommunityCards(3);
        if (!bettingPhase(aiGoesFirst)) return;

        currentStage = RoundStage.TURN;
        dealCommunityCards(1);
        if (!bettingPhase(aiGoesFirst)) return;

        currentStage = RoundStage.RIVER;
        dealCommunityCards(1);
        if (!bettingPhase(aiGoesFirst)) return;

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
            printLines();
            HandRank aiHandRank = null;
            if (currentStage == RoundStage.PRE_FLOP) {
                aiHandRank = HandEvaluator.evaluateHand(ai.getHand().getCards());
            } else {
                aiHandRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));
            }

            if (aiHandRank == null) {
                throw new IllegalStateException("AI hand evaluation failed.");
            }

            action = pokerAI.getAIDecision(aiHandRank, currentBet, pot, currentStage);
//            System.out.println("AI`s turn:" + action);
        }

        else {
            action = getPlayerDecision(currentBet, player.getChips());
            System.out.println(player.getName() + " chooses: " + action);
        }

        if (action == BettingAction.FOLD) {
            System.out.println(player.getName() + " folded.");
            return -1;
        }

        if (action == BettingAction.ALL_IN) {
            System.out.println(player.getName() + " goes ALL-IN with " + player.getChips() + " chips!");
            if (isAI) aiAllIn = true;
            else playerAllIn = true;
            currentBet = player.getChips();
            pot += player.getChips();
            player.addChips(-player.getChips());
            return currentBet;
        }

        if (action == BettingAction.RAISE) {
            int raiseAmount = getRaiseAmount(player.getChips(), currentBet);
            if (raiseAmount > 0) {
                player.addChips(-raiseAmount);
                pot += raiseAmount;
                currentBet += raiseAmount;
                System.out.println(player.getName() + " raises by " + raiseAmount + " chips!");
                return currentBet;
            } else {
                System.out.println("Invalid raise amount. Try again.");
                return processBettingTurn(player, isAI, currentBet);
            }
        }

        if (action == BettingAction.CHECK) {
            System.out.println(player.getName() + " checks.");
            System.out.println("Current pot: " + pot);
            return currentBet;
        }

        if (action == BettingAction.CALL) {
            int callAmount = currentBet;
            if (callAmount > player.getChips()) {
                callAmount = player.getChips();
            }

            player.addChips(-callAmount);
            pot += callAmount;
            System.out.println(player.getName() + " calls the bet of " + callAmount + " chips.");
            System.out.println("current pot: " + pot);
            return currentBet;
        }

        return currentBet;
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

    private boolean bettingPhase(boolean aiGoesFirst) {
        printLines();
        System.out.println("\n--- " + currentStage + " Betting Phase ---");
        printChipCounts();
        human.showCards();

        int currentBet = 0;
        printPot(currentBet);

        System.out.println("\n[DEBUG] Ai first:" + aiGoesFirst);

        Player firstPlayer = aiGoesFirst ? ai : human;
        Player secondPlayer = aiGoesFirst ? human : ai;
        boolean firstIsAI = aiGoesFirst;

        int newBet = processBettingTurn(firstPlayer, firstIsAI, currentBet);
        if (newBet == -1) return handleFold(secondPlayer);
        if (playerAllIn || aiAllIn) return handleAllInScenario();

        // Second player responds
        int secondBet = processBettingTurn(secondPlayer, !firstIsAI, newBet);
        if (secondBet == -1) return handleFold(firstPlayer);
        if (playerAllIn || aiAllIn) return handleAllInScenario();

        // If second player raises, first player must act again
        if (secondBet > newBet) {
            newBet = processBettingTurn(firstPlayer, firstIsAI, secondBet);
            if (newBet == -1) return handleFold(secondPlayer);
            if (playerAllIn || aiAllIn) return handleAllInScenario();
        }


        return true;
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
