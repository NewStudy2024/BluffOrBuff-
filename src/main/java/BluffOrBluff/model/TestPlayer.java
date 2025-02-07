package BluffOrBluff.model;

import BluffOrBluff.exception.GameException;

public class TestPlayer {
    public static void main(String[] args) {
        try {
            Player player = new Player("Atai", 2000);
            System.out.println("Playe sucessfully created: " + player.getName());
            System.out.println("Amount of chips: " + player.getChips());

            Deck deck = new Deck();

            player.receiveCard(deck.dealCard());
            player.receiveCard(deck.dealCard());

            player.showCards();

            player.resetHand();

            player.showCards();

            player.receiveCard(deck.dealCard());
            player.receiveCard(deck.dealCard());

            player.showCards();

            System.out.println("Adding 1000 chips");
            player.addChips(1000);
            System.out.println("Amount of chips: " + player.getChips());

            System.out.println("betting 500 chips");
            player.placeBet(500);
            System.out.println("Amount of chips: " + player.getChips());

            System.out.println("Trying to add a third card: ");
            player.receiveCard(deck.dealCard());
        } catch (GameException e) {
            System.out.println("⚠ Error: " + e.getMessage());
        }
    }
}
