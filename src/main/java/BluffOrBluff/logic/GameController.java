package BluffOrBluff.logic;

import BluffOrBluff.model.*;
import BluffOrBluff.menu.GameMenu;
import BluffOrBluff.util.InputHandler;

public class GameController {
    private final Player human;
    private final Player ai;
    private int difficulty;

    public GameController(int difficulty) {
        String playerName = GameMenu.askForPlayerName();
        int startingChips = GameMenu.askForStartingChips();

        this.human = new Player(playerName, startingChips);
        this.ai = new Player("AI", startingChips);
        this.difficulty = difficulty;
    }

    public void startGame() {
        System.out.println("Welcome to Bluff or Buff - Texas Hold'em!");
        System.out.println(human.getName() + " starts with " + human.getChips() + " chips.");

        while (true) {
            if (human.getChips() <= 0) {
                System.out.println("You are out of chips! AI wins the game.");
                break;
            } else if (ai.getChips() <= 0) {
                System.out.println("AI is out of chips! You win the game!");
                break;
            }
            playRound();
            if (!handlePostRoundOptions()) break;
        }
    }

    private void playRound() {
        Deck deck = new Deck();
        deck.shuffle();
        RoundManager roundManager = new RoundManager(deck, human, ai, difficulty);
        roundManager.playRound();
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
                System.out.println("Thanks for playing!");
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