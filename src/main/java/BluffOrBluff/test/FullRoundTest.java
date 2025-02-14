package BluffOrBluff.test;

import BluffOrBluff.model.*;
import BluffOrBluff.logic.RoundManager;

public class FullRoundTest {
    public static void main(String[] args) {
        Player human = new Player("Tester", 10000);
        Player ai = new Player("AI Bot", 10000);

        Deck deck = new Deck();

        int numRounds = 5;
        for (int round = 1; round <= numRounds; round++) {
            System.out.println("\n===== Starting Round " + round + " =====");

            RoundManager roundManager = new RoundManager(deck, human, ai, 1);
            roundManager.playRound();

            System.out.println("\n--- After Round " + round + " ---");
            System.out.println("Player chips: " + human.getChips());
            System.out.println("AI chips: " + ai.getChips());
            System.out.println("----------------------------");

            if (human.getChips() <= 0 || ai.getChips() <= 0) {
                System.out.println("Game over! One player is out of chips.");
                break;
            }
        }
    }
}
