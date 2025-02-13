package BluffOrBluff.model;

import java.util.List;

public class HandRank implements Comparable<HandRank> {
    private final String rank;
    private final List<Integer> highCards;

    public HandRank(String rank, List<Integer> highCards) {
        this.rank = rank;
        this.highCards = highCards;
    }

    public String getRank() {
        return rank;
    }

    public List<Integer> getHighCards() {
        return highCards;
    }

    @Override
    public int compareTo(HandRank other) {
        int rankComparison = Integer.compare(
                HandEvaluator.getHandRankValue(this.rank),
                HandEvaluator.getHandRankValue(other.rank)
        );
        if (rankComparison != 0) return rankComparison;

        for (int i = 0; i < Math.min(this.highCards.size(), other.highCards.size()); i++) {
            int cardComparison = Integer.compare(this.highCards.get(i), other.highCards.get(i));
            if (cardComparison != 0) {
                return cardComparison;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return rank + " (High cards: " + highCards + ")";
    }
}
