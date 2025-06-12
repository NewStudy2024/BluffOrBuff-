package BluffOrBluff.model;

import java.util.*;

public class HandEvaluator {

    public static HandRank evaluateHand(List<Card> playerHand) {
        if (playerHand.size() > 7) {
            throw new IllegalArgumentException("A valid poker hand should have at most 7 cards.");
        }

        // Count occurrences of each rank
        Map<Card.Rank, Integer> rankCount = new HashMap<>();
        for (Card card : playerHand) {
            rankCount.put(card.getRank(), rankCount.getOrDefault(card.getRank(), 0) + 1);
        }

        List<Integer> highCards = getSortedRanks(playerHand);

        if (isRoyalFlush(playerHand)) return new HandRank("Royal Flush", highCards);
        if (isStraightFlush(playerHand)) return new HandRank("Straight Flush", highCards);
        if (hasNOfAKind(rankCount, 4)) return new HandRank("Four of a Kind", getHighCardForRank(rankCount, 4));
        if (hasFullHouse(rankCount)) return new HandRank("Full House", getFullHouseRanks(rankCount));
        if (isFlush(playerHand)) return new HandRank("Flush", highCards);
        if (isStraight(playerHand)) return new HandRank("Straight", highCards);
        if (hasNOfAKind(rankCount, 3)) return new HandRank("Three of a Kind", getHighCardForRank(rankCount, 3));
        if (hasTwoPair(rankCount)) return new HandRank("Two Pair", getTwoPairRanks(rankCount, playerHand));
        if (hasNOfAKind(rankCount, 2)) return new HandRank("One Pair", getHighCardForRank(rankCount, 2));
        return new HandRank("High Card", highCards);

    }

    private static List<Integer> getSortedRanks(List<Card> hand) {
        return hand.stream()
                .map(c -> c.getRank().ordinal())
                .sorted(Comparator.reverseOrder())
                .limit(5)
                .toList();
    }

    private static List<Integer> getHighCardForRank(Map<Card.Rank, Integer> rankCount, int n) {
        return rankCount.entrySet().stream()
                .filter(entry -> entry.getValue() == n)
                .map(entry -> entry.getKey().ordinal())
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    private static List<Integer> getFullHouseRanks(Map<Card.Rank, Integer> rankCount) {
        List<Integer> threeCards = new ArrayList<>(getHighCardForRank(rankCount, 3));
        List<Integer> twoCards = getHighCardForRank(rankCount, 2);
        threeCards.addAll(twoCards);
        return threeCards;
    }

    private static List<Integer> getTwoPairRanks(Map<Card.Rank, Integer> rankCount, List<Card> playerHand) {
        List<Integer> pairs = getHighCardForRank(rankCount, 2);
        List<Integer> highCards = getSortedRanks(playerHand);

        List<Integer> result = new ArrayList<>(pairs.subList(0, Math.min(2, pairs.size())));

        for (int card : highCards) {
            if (!result.contains(card)) {
                result.add(card);
                break;
            }
        }

        return result;
    }

    private static boolean hasNOfAKind(Map<Card.Rank, Integer> rankCount, int n) {
        return rankCount.containsValue(n);
    }

    private static boolean hasFullHouse(Map<Card.Rank, Integer> rankCount) {
        return hasNOfAKind(rankCount, 3) && hasNOfAKind(rankCount, 2);
    }

    private static boolean hasTwoPair(Map<Card.Rank, Integer> rankCount) {
        return getHighCardForRank(rankCount, 2).size() >= 2;
    }

    private static boolean isFlush(List<Card> hand) {
        Map<Card.Suit, List<Integer>> suitCards = new HashMap<>();

        for (Card card : hand) {
            suitCards.putIfAbsent(card.getSuit(), new ArrayList<>());
            suitCards.get(card.getSuit()).add(card.getRank().ordinal());
        }

        for (List<Integer> ranks : suitCards.values()) {
            if (ranks.size() >= 5) {
                ranks.sort(Collections.reverseOrder());
                return true;
            }
        }
        return false;
    }

    private static boolean isStraight(List<Card> hand) {
        Set<Integer> ranks = new HashSet<>();
        for (Card card : hand) {
            ranks.add(card.getRank().ordinal());
        }

        if (ranks.contains(12)) {
            ranks.add(-1);
        }

        List<Integer> sortedRanks = new ArrayList<>(ranks);
        Collections.sort(sortedRanks);

        int consecutive = 1;
        for (int i = 1; i < sortedRanks.size(); i++) {
            if (sortedRanks.get(i) - sortedRanks.get(i - 1) == 1) {
                consecutive++;
                if (consecutive >= 5) return true;
            } else if (sortedRanks.get(i) != sortedRanks.get(i - 1)) {
                consecutive = 1;
            }
        }
        return false;
    }

    private static List<List<Card>> getFiveCardCombos(List<Card> hand) {
        List<List<Card>> combos = new ArrayList<>();
        int n = hand.size();
        if (n < 5) {
            combos.add(new ArrayList<>(hand));
            return combos;
        }
        for (int i = 0; i < n - 4; i++) {
            for (int j = i + 1; j < n - 3; j++) {
                for (int k = j + 1; k < n - 2; k++) {
                    for (int l = k + 1; l < n - 1; l++) {
                        for (int m = l + 1; m < n; m++) {
                            combos.add(List.of(
                                    hand.get(i), hand.get(j), hand.get(k),
                                    hand.get(l), hand.get(m)));
                        }
                    }
                }
            }
        }
        return combos;
    }

    private static boolean isStraightFlush(List<Card> hand) {
        for (List<Card> combo : getFiveCardCombos(hand)) {
            if (isFlush(combo) && isStraight(combo)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRoyalFlush(List<Card> hand) {
        List<Card.Rank> royalRanks = List.of(Card.Rank.TEN, Card.Rank.JACK, Card.Rank.QUEEN, Card.Rank.KING, Card.Rank.ACE);
        for (List<Card> combo : getFiveCardCombos(hand)) {
            if (combo.stream().allMatch(card -> royalRanks.contains(card.getRank()))
                    && isFlush(combo) && isStraight(combo)) {
                return true;
            }
        }
        return false;
    }

    public static int getPreFlopHandStrength(List<Card> holeCards) {
        if (holeCards.size() != 2) {
            throw new IllegalArgumentException("Pre-flop hand must have exactly 2 cards.");
        }

        Card.Rank rank1 = holeCards.get(0).getRank();
        Card.Rank rank2 = holeCards.get(1).getRank();
        boolean suited = holeCards.get(0).getSuit() == holeCards.get(1).getSuit();

        // Use a simple pre-flop ranking (higher number = stronger hand)
        if ((rank1 == Card.Rank.ACE && rank2 == Card.Rank.KING) || (rank1 == Card.Rank.KING && rank2 == Card.Rank.ACE)) {
            return suited ? 9 : 8; // AK suited is stronger
        }
        if (rank1 == rank2) { // Pocket pairs
            return 7 + rank1.ordinal();
        }
        if ((rank1 == Card.Rank.ACE || rank2 == Card.Rank.ACE) && (rank1.ordinal() >= Card.Rank.TEN.ordinal() || rank2.ordinal() >= Card.Rank.TEN.ordinal())) {
            return 7; // A-Q, A-J
        }
        if (rank1.ordinal() >= Card.Rank.TEN.ordinal() && rank2.ordinal() >= Card.Rank.TEN.ordinal()) {
            return suited ? 7 : 6; // High connectors (J-Q, Q-K)
        }
        return 1; // Default weak hand
    }


    public static int getHandRankValue(String rank) {
        return switch (rank) {
            case "Royal Flush" -> 10;
            case "Straight Flush" -> 9;
            case "Four of a Kind" -> 8;
            case "Full House" -> 7;
            case "Flush" -> 6;
            case "Straight" -> 5;
            case "Three of a Kind" -> 4;
            case "Two Pair" -> 3;
            case "One Pair" -> 2;
            default -> 1;
        };
    }
}
