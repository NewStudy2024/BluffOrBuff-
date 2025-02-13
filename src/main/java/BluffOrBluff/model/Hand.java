package BluffOrBluff.model;

import BluffOrBluff.exception.GameException;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) throws GameException {
        if (cards.size() < 2) {
            cards.add(card);
        } else {
            throw new GameException("Cannot add more than two hole cards to a hand.");
        }
    }

    public void resetHand() {
        cards.clear();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public List<Card> getFullHand(List<Card> communityCards) {
        List<Card> fullHand = new ArrayList<>(this.cards);
        fullHand.addAll(communityCards);
        return fullHand;
    }


    @Override
    public String toString() {
        return cards.toString();
    }
}

