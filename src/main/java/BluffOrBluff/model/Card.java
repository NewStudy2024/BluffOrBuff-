package BluffOrBluff.model;

public class Card {
    public enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
    public enum Rank {
        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
        JACK, QUEEN, KING, ACE
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        String suitSymbol = switch (suit) {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
            default -> "?";
        };
        return rank + " " + suitSymbol;
    }

    // Method to test if the class works
    public static void test(String[] args) {
        Card card1 = new Card(Card.Rank.QUEEN, Card.Suit.HEARTS);
        Card card2 = new Card(Card.Rank.TEN, Card.Suit.CLUBS);

        System.out.println("Card 1: " + card1);
        System.out.println("Card 2: " + card2);

        System.out.println("Card 1 Rank: " + card1.getRank());
        System.out.println("Card 2 Suit: " + card2.getSuit());
    }

}


