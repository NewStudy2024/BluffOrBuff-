package BluffOrBluff.menu;

import java.util.Scanner;
import BluffOrBluff.exception.GameException;

public class GameMenu {
    private static final Scanner scanner = new Scanner(System.in);

    // Displays the main menu options
    public static void showMainMenu() {
        System.out.println("\n=== Bluff or Buff: Poker Game ===");
        System.out.println("1. Start Game");
        System.out.println("2. Rules");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
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

    public static int getMenuChoice() throws GameException {
        while (true) {
            return optionSelector();
        }
    }


    public static void exitGame() {
        System.out.println("\nExiting game. Resetting everything...");

        // Reset scanner
        scanner.close();

        // TODO : reset everything

        System.out.println("Goodbye! See you next time.");
        System.exit(0); // Forcefully exits the program
    }

    public static int askBetweenGames() {
        while (true) {
            System.out.println("\n=== What would you like to do next? ===");
            System.out.println("1. Play another round");
            System.out.println("2. Change difficulty");
            System.out.println("3. Exit game");
            System.out.print("Choose an option: ");

            optionSelector();
        }
    }

    private static int optionSelector() {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());

            return switch (choice) {
                case 1, 2, 3 -> choice;
                default -> {
                    System.out.print("Invalid choice. Please enter 1, 2, or 3: ");
                    yield -1;
                }
            };
        } catch (NumberFormatException e) {
            System.out.print("Invalid input. Please enter a number (1, 2, or 3): ");
            return -1;
        }
    }


}

