package BluffOrBluff.menu;

import BluffOrBluff.util.InputHandler;
import java.util.Scanner;

public class GameMenu {
    private static final Scanner scanner = new Scanner(System.in);

    public static void showMainMenu() {
        System.out.println("\n=== Bluff or Buff: Poker Game ===");
        System.out.println("1. Start Game");
        System.out.println("2. Rules");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    public static int getMenuChoice() {
        return InputHandler.getValidInt(1, 3);
    }

    public static void showRules() {
        System.out.println("\n========================== Poker Rules ==========================");
        System.out.println("1. Each player starts with two private cards.");
        System.out.println("2. Five community cards are dealt in stages (Flop, Turn, River).");
        System.out.println("3. Players bet, call, raise, or fold in each round.");
        System.out.println("4. The best five-card hand wins at showdown.");
        System.out.println("5. Bluffing is key! Don't let AI read you.");
        System.out.println("==================================================================");
    }

    public static void exitGame() {
        System.out.println("\nExiting game. Resetting everything...");
        scanner.close();
        System.out.println("Goodbye! See you next time.");
        System.exit(0);
    }

        public static void showPostRoundMenu() {
        System.out.println("\n=== What would you like to do next? ===");
        System.out.println("1. Play another round");
        System.out.println("2. Change difficulty");
        System.out.println("3. Exit game");
    }

    public static int getPostRoundChoice() {
        return InputHandler.getValidInt(1, 3);
    }

    public static String askForPlayerName() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine().trim();
        return name.isEmpty() ? "Player" : name;
    }

    public static int askForStartingChips() {
        System.out.print("\nEnter your starting chips (minimum 500, maximum 10000): ");
        return InputHandler.getValidInt(500, 10000);
    }

    public static int getDifficultySelection() {
        System.out.println("Select difficulty level:");
        System.out.println("[1] Beginner");
        System.out.println("[2] Normal");
        System.out.println("[3] Expert");
        return InputHandler.getValidInt(1, 3);
    }
}
