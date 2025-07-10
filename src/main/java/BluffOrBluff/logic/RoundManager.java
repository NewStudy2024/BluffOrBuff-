package BluffOrBluff.logic;

import BluffOrBluff.model.*;
import BluffOrBluff.exception.GameException;
import BluffOrBluff.util.InputHandler;
import BluffOrBluff.ai.PokerAI;

import java.util.ArrayList;
import java.util.List;

public class RoundManager {
    // Constants for better maintainability
    private static final int MIN_BET = 50;
    private static final int MIN_RAISE = 50;
    private static final int MAX_COMMUNITY_CARDS = 5;
    private static final int HOLE_CARDS_COUNT = 2;
    
    // Core game components
    private final PokerAI pokerAI;
    private Deck deck;
    private final Player human;
    private final Player ai;
    private final List<Card> communityCards = new ArrayList<>();
    
    // Game state
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

        // PRE-FLOP: No community cards yet
        currentStage = RoundStage.PRE_FLOP;
        if (!bettingPhase()) return;
        
        addGameDelay(1000); // 1 second delay before flop

        // FLOP: Deal 3 community cards BEFORE betting
        currentStage = RoundStage.FLOP;
        System.out.println("\n🃏 Dealing the FLOP...");
        addGameDelay(800); // Short pause for drama
        dealCommunityCards(3);
        if (!bettingPhase()) return;
        
        addGameDelay(1000); // 1 second delay before turn

        // TURN: Deal 1 more community card BEFORE betting  
        currentStage = RoundStage.TURN;
        System.out.println("\n🃏 Dealing the TURN...");
        addGameDelay(800); // Short pause for drama
        dealCommunityCards(1);
        if (!bettingPhase()) return;
        
        addGameDelay(1000); // 1 second delay before river

        // RIVER: Deal final community card BEFORE betting
        currentStage = RoundStage.RIVER;
        System.out.println("\n🃏 Dealing the RIVER...");
        addGameDelay(800); // Short pause for drama
        dealCommunityCards(1);
        if (!bettingPhase()) return;

        // SHOWDOWN
        currentStage = RoundStage.SHOWDOWN;
        addGameDelay(500); // Short delay before showdown
        determineRoundWinner();

        roundCounter++;  // Move to the next round after a full hand is completed
    }

    private void resetRound() {
        // Reuse existing deck instead of creating new one for better performance
        if (deck.cardsLeft() < 10) { // Only create new deck if running low on cards
            this.deck = new Deck();
        } else {
            deck.shuffle(); // Just reshuffle existing deck
        }
        
        // Reset game state
        human.resetHand();
        ai.resetHand();
        communityCards.clear();
        pot = 0;
        currentStage = RoundStage.PRE_FLOP;
        playerAllIn = false;
        aiAllIn = false;
    }

    private void dealHoleCards() throws GameException {
        if (deck.cardsLeft() < HOLE_CARDS_COUNT * 2) {
            throw new GameException("Not enough cards in deck to deal hole cards");
        }
        
        try {
            human.receiveCard(deck.dealCard());
            human.receiveCard(deck.dealCard());
            ai.receiveCard(deck.dealCard());
            ai.receiveCard(deck.dealCard());
            
            human.showCards();
            System.out.println("AI's hole cards: " + ai.getHand());
        } catch (GameException e) {
            throw new GameException("Error dealing hole cards: " + e.getMessage());
        }
    }

    private void dealCommunityCards(int numCards) {
        if (numCards <= 0 || communityCards.size() + numCards > MAX_COMMUNITY_CARDS) {
            throw new IllegalArgumentException("Invalid number of community cards to deal");
        }
        
        if (deck.cardsLeft() < numCards) {
            throw new IllegalStateException("Not enough cards in deck");
        }
        
        for (int i = 0; i < numCards; i++) {
            communityCards.add(deck.dealCard());
        }
        // Cards will be displayed in the betting phase, not here
    }

    private void printChipCounts() {
        StringBuilder sb = new StringBuilder("\nChip Counts:\n");
        sb.append(human.getName()).append(" Chips: ").append(human.getChips()).append("\n");
        sb.append("AI Chips: ").append(ai.getChips());
        System.out.println(sb.toString());
    }

    private void printGameState(int currentBet) {
        System.out.println("Total pot: " + pot + " chips" + 
                          (currentBet > 0 ? " (Current Bet: " + currentBet + ")" : ""));
    }

    private void printLines(){
        System.out.println("\n".repeat(3));
    }

    private int processBettingTurn(Player player, boolean isAI, int currentBet) {
        BettingAction action = getBettingAction(player, isAI, currentBet);
        return executeBettingAction(player, isAI, action, currentBet);
    }
    
    private BettingAction getBettingAction(Player player, boolean isAI, int currentBet) {
        if (isAI) {
            HandRank aiHandRank = HandEvaluator.evaluateHand(ai.getFullHand(communityCards));
            return pokerAI.getAIDecision(aiHandRank, currentBet, pot, currentStage, communityCards);
        } else {
            BettingAction action = getPlayerDecision(currentBet, player.getChips());
            System.out.println(player.getName() + " chooses: " + action);
            return action;
        }
    }
    
    private int executeBettingAction(Player player, boolean isAI, BettingAction action, int currentBet) {
        switch (action) {
            case FOLD:
                return handleFoldAction(player);
                
            case ALL_IN:
                return handleAllInAction(player, isAI);
                
            case RAISE:
                return handleRaiseAction(player, isAI, currentBet);
                
            case CALL:
                return handleCallAction(player, currentBet);
                
            case CHECK:
                return handleCheckAction(player, currentBet);
                
            case BET:
                return handleBetAction(player, isAI);
                
            default:
                System.out.println("Invalid action by " + player.getName() + ".");
                return currentBet;
        }
    }
    
    private int handleFoldAction(Player player) {
        System.out.println(player.getName() + " folded.");
        return -1;
    }
    
    private int handleAllInAction(Player player, boolean isAI) {
        int allInAmount = player.getChips();
        System.out.println(player.getName() + " goes ALL-IN with " + allInAmount + " chips!");
        
        if (isAI) aiAllIn = true;
        else playerAllIn = true;
        
        if (player.placeBet(allInAmount)) {
            pot += allInAmount;
            return allInAmount;
        } else {
            System.out.println("Error: " + player.getName() + " couldn't place all-in bet!");
            return -1;
        }
    }
    
    private int handleRaiseAction(Player player, boolean isAI, int currentBet) {
        int raiseAmount = calculateRaiseAmount(player, isAI, currentBet);
        
        if (player.placeBet(raiseAmount)) {
            pot += raiseAmount;
            printRaiseMessage(player, raiseAmount, currentBet);
            return raiseAmount;
        } else {
            System.out.println("Error: " + player.getName() + " couldn't place raise bet!");
            return -1;
        }
    }
    
    private int calculateRaiseAmount(Player player, boolean isAI, int currentBet) {
        if (isAI) {
            int baseRaise = MIN_RAISE + (difficulty * 25);
            if (currentBet > 0) {
                return Math.min(currentBet + baseRaise, player.getChips());
            } else {
                return Math.min(baseRaise, player.getChips());
            }
        } else {
            return getRaiseAmount(player.getChips(), currentBet);
        }
    }
    
    private void printRaiseMessage(Player player, int raiseAmount, int currentBet) {
        if (currentBet > 0) {
            System.out.println(player.getName() + " raises to " + raiseAmount + 
                             " chips (+" + (raiseAmount - currentBet) + " raise)!");
        } else {
            System.out.println(player.getName() + " raises by " + raiseAmount + " chips!");
        }
    }
    
    private int handleCallAction(Player player, int currentBet) {
        int callAmount = Math.min(currentBet, player.getChips());
        if (player.placeBet(callAmount)) {
            pot += callAmount;
            System.out.println(player.getName() + " calls the bet of " + callAmount + " chips.");
            return currentBet;
        } else {
            System.out.println(player.getName() + " doesn't have enough chips to call!");
            return -1;
        }
    }
    
    private int handleCheckAction(Player player, int currentBet) {
        System.out.println(player.getName() + " checks.");
        return currentBet;
    }
    
    private int handleBetAction(Player player, boolean isAI) {
        int betAmount = isAI ? 
            Math.min(MIN_BET + (difficulty * 25), player.getChips()) :
            getRaiseAmount(player.getChips(), 0);
            
        if (player.placeBet(betAmount)) {
            pot += betAmount;
            System.out.println(player.getName() + " bets " + betAmount + " chips!");
            return betAmount;
        } else {
            System.out.println("Error: " + player.getName() + " couldn't place bet!");
            return -1;
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
        System.out.println("\n=== " + currentStage + " BETTING PHASE ===");
        
        // Show community cards prominently if any exist
        if (!communityCards.isEmpty()) {
            System.out.println("🃏 COMMUNITY CARDS: " + communityCards);
            System.out.println(); // Extra line for spacing
        }
        
        printChipCounts();
        human.showCards();

        int currentBet = 0;
        System.out.println("\n[Your Turn] Choose your action:");

        // **Human makes the first move**
        currentBet = processBettingTurn(human, false, currentBet);
        if (currentBet == -1) return handleFold(ai); // Human folded
        if (playerAllIn) return askForAllInDecision(ai, true, currentBet); // If human is all-in, AI must react

        // **AI responds** 
        System.out.println("\n[AI Turn]");
        addGameDelay(500); // Brief pause for AI to "think"
        int aiBet = processBettingTurn(ai, true, currentBet);
        if (aiBet == -1) return handleFold(human); // AI folded
        if (aiAllIn) return askForAllInDecision(human, false, aiBet); // If AI goes all-in, human must react

        // **Loop continues only if AI raises**
        while (aiBet > currentBet) {
            System.out.println("\n[Your Turn] AI raised - your response:");
            currentBet = processBettingTurn(human, false, aiBet);
            if (currentBet == -1) return handleFold(ai);
            if (playerAllIn) return askForAllInDecision(ai, true, currentBet);

            System.out.println("\n[AI Turn]");
            addGameDelay(500); // Brief pause for AI to "think"
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
            choice = pokerAI.decideAllInCall(currentBet, pot, aiHandRank, difficulty);
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
        int cardsNeeded = MAX_COMMUNITY_CARDS - communityCards.size();
        if (cardsNeeded > 0) {
            dealCommunityCards(cardsNeeded);
        }
    }

    private int getRaiseAmount(int playerChips, int currentBet) {
        int minRaise = currentBet == 0 ? MIN_RAISE : currentBet + MIN_RAISE;
        System.out.println("Enter raise amount (Minimum: " + minRaise + 
                          " chips, Maximum: " + playerChips + " chips):");
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

        // Determine winner and distribute chips
        String winner = determineWinnerAndDistributeChips(humanHandRank, aiHandRank);
        System.out.println(winner);

        // Reset all-in flags for next round
        resetAllInFlags();

        // Display final chip counts
        displayFinalChipCounts();
    }
    
    private String determineWinnerAndDistributeChips(HandRank humanHandRank, HandRank aiHandRank) {
        int comparison = humanHandRank.compareTo(aiHandRank);
        
        if (comparison > 0) {
            human.addChips(pot);
            return human.getName() + " wins with " + humanHandRank;
        } else if (comparison < 0) {
            ai.addChips(pot);
            return "AI wins with " + aiHandRank;
        } else {
            int halfPot = pot / 2;
            human.addChips(halfPot);
            ai.addChips(halfPot);
            return "It's a tie! Both have " + humanHandRank;
        }
    }
    
    private void resetAllInFlags() {
        playerAllIn = false;
        aiAllIn = false;
    }
    
    private void displayFinalChipCounts() {
        StringBuilder sb = new StringBuilder();
        sb.append("AI chips: ").append(ai.getChips()).append("\n");
        sb.append(human.getName()).append(" chips: ").append(human.getChips());
        System.out.println(sb.toString());
    }
    
    /**
     * Adds a brief delay to make the game flow feel more natural
     */
    private void addGameDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Continue without delay if interrupted
        }
    }
}
