package BluffOrBluff.logic;


import BluffOrBluff.model.*;

public class TestBetting {
    public static void main(String[] args) {
        Player human = new Player("Peter", 10000);
        Player ai = new Player("AI Bot", 10000);
        Deck deck = new Deck();
        RoundManager roundManager = new RoundManager(deck, human, ai, 1);

        try {
            roundManager.playRound();
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
    }
}
