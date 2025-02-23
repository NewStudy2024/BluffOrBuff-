package BluffOrBluff.logic;

import BluffOrBluff.model.*;
import BluffOrBluff.menu.GameMenu;

public class GameController {
    private final Player human;
    private final Player ai;
    private final Deck deck;
    private final RoundManager roundManager;
    private int difficulty;

    public GameController(String playerName, int difficulty) {
        this.human = new Player(playerName, 1000); // Default starting chips //TODO: ask player how much money they want to start with
        this.ai = new Player("AI", 1000);
        this.difficulty = difficulty;

        this.deck = new Deck();
        deck.shuffle();
        this.roundManager = new RoundManager(deck, human, ai, difficulty);
    }

    public void startGame() {
        System.out.println("Welcome to Bluff or Buff - Texas Hold'em!");
        System.out.println(human.getName() + " starts with " + human.getChips() + " chips."); //TODO: change this to ask how much money they want to start with

        while (true) {
            if (human.getChips() <= 0) {
                System.out.println("You are out of chips! AI wins the game.");
                break;
            } else if (ai.getChips() <= 0) {
                System.out.println("AI is out of chips! You win the game!");
                break;
            }
            roundManager.playRound();
            if (!handlePostRoundOptions()) break;
        }
    }

    private boolean handlePostRoundOptions() {
        GameMenu.showPostRoundMenu();
        int choice = GameMenu.getPostRoundChoice();
        return switch (choice) {
            case 1 -> true;
            case 2 -> {
                changeDifficulty();
                yield true;
            }
            case 3 -> {
                GameMenu.exitGame();
                yield false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        };
    }

    private void changeDifficulty() {
        difficulty = GameMenu.getDifficultySelection();
        System.out.println("Difficulty set to " + (difficulty == 1 ? "Beginner" : difficulty == 2 ? "Normal" : "Expert"));
    }
}
