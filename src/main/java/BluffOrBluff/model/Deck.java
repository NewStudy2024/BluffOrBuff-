package BluffOrBluff.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values() ){
                cards.add( new Card(rank, suit));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards to deal");
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int cardsLeft() {
        return cards.size();
    }

    public void printDeck() {
        for (Card card : cards) {
            System.out.print(card + " ");
        }
    }

    public void removeCards(List<Card> cardsToRemove) {
        cards.removeAll(cardsToRemove);
    }


    public static void testDeck(Deck deck) {
        deck.printDeck();
        deck.shuffle();
        System.out.println();
        deck.printDeck();
        System.out.println("\n");

        while (!deck.isEmpty()) {
            System.out.println(deck.dealCard() + "  " + deck.cardsLeft());
        }
        System.out.println("\nAll cards have been dealt!");
    }

    public static void main (String[] args) {
        testDeck(new Deck());
    }

}


