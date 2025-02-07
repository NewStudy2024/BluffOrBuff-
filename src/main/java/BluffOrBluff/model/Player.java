package BluffOrBluff.model;

import BluffOrBluff.exception.GameException;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int chips;
    private List<Card> holeCards;

    public Player (String name, int chips) {
        this.name = name;
        this.chips = chips;
        this.holeCards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public  void receiveCard(Card card) throws GameException {
        if (holeCards.size() >= 2) {
            throw new GameException(name + " already has 2 hole cards!");
        }
        holeCards.add(card);
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
        System.out.println(name + "'s hole cards: " + holeCards);
    }

    public void resetHand() {
        holeCards.clear();
    }

}
