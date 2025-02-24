package BluffOrBluff.model;

import BluffOrBluff.exception.GameException;
import java.util.List;

public class Player {
    private final String name;
    private int chips;
    private Hand hand;

    public Player (String name, int chips) {
        this.name = name;
        this.chips = chips;
        this.hand = new Hand();
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void moneyLost() {
        chips = 0;
    }

    public Hand getHand() {
        return hand;
    }

    public  void receiveCard(Card card) throws GameException {
        hand.addCard(card);
    }

    public boolean placeBet(int amount) {
        if (amount > chips) {
            System.out.println(name + " does not have enough to bet " + amount + "!");
            return false;
        }
        chips -= amount;
        return true;
    }

    public void addChips(int amount) {
        chips += amount;
    }

    public void showCards() {
        System.out.println(name + "'s hole cards: " + hand);
    }

    public List<Card> getFullHand(List<Card> communityCards) {
        return hand.getFullHand(communityCards);
    }

    public void resetHand() {
        hand.resetHand();
    }

}
