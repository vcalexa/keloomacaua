package com.sound.keloomacaua.game;

import android.content.Context;

import androidx.annotation.DrawableRes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

public class CardUtils implements Serializable {

    public static final String CARD_TWO = "two";
    public static final String CARD_THREE = "three";
    public static final String CARD_FOUR = "four";
    public static final String CARD_JOKER = "joker";
    public static final String CARD_ACE = "ace";

    public static final String SUITE_CLUBS = "clubs";
    public static final String SUITE_DIAMONDS = "diamonds";
    public static final String SUITE_HEARTS = "hearts";
    public static final String SUITE_SPADES = "spades";

    public static final String SUITE_BLACK = "black";
    public static final String SUITE_RED = "red";

    private static final String[] SUITS = {SUITE_CLUBS, SUITE_DIAMONDS, SUITE_HEARTS, SUITE_SPADES};
    private static final String[] JOKER_SUITS = {SUITE_BLACK, SUITE_RED};
    private static final String[] RANKS = {CARD_ACE, CARD_TWO, CARD_THREE, CARD_FOUR, "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king"};
    private static final int CARDS_PER_DECK = JOKER_SUITS.length + SUITS.length * RANKS.length;

    public static Map<String, Integer> nameToCard;
    public static Map<Integer, Pair<String, String>> cardToPair;

    static {
        int i = 0;
        cardToPair = new HashMap<>(CARDS_PER_DECK);
        nameToCard = new HashMap<>(CARDS_PER_DECK);
        for (String rank : RANKS) {
            for (String suite : SUITS) {
                cardToPair.put(i, new Pair<>(rank, suite));
                nameToCard.put(rank + "_of_" + suite, i);
                i++;
            }
        }
        for (String suite : JOKER_SUITS) {
            cardToPair.put(i, new Pair<>(CARD_JOKER, suite));
            nameToCard.put(CARD_JOKER + "_of_" + suite, i);
            i++;
        }
    }

    public static String getImageViewName(int n) {
        if (n < 0) {
            return "card_back";
        } else {
            Pair<String, String> card = cardToPair.get(n);
            return String.format("%s_of_%s", card.getFirst(), card.getSecond());
        }
    }

    public static String getCardRank(int cardNumber) {
        return cardToPair.get(cardNumber).getFirst();
    }

    public static String getCardSuite(int cardNumber) {
        return cardToPair.get(cardNumber).getSecond();
    }

    public static boolean hasSameSuite(int card1, int card2) {
        return getCardSuite(card1).equals(getCardSuite(card2));
    }

    public static boolean hasSameRank(int card1, int card2) {
        return getCardRank(card1).equals(getCardRank(card2));
    }

    public static boolean cardHasRank(int card, String rank) {
        return getCardRank(card).equals(rank);
    }

    public static @DrawableRes
    int cardToImageId(int card, Context context) {
        String imageTitleFromCard = CardUtils.getImageViewName(card);
        return context.getResources().getIdentifier(imageTitleFromCard, "drawable", context.getPackageName());
    }

    public static @DrawableRes
    int suiteToImageId(String suite, Context context) {
        return context.getResources().getIdentifier(suite, "drawable", context.getPackageName());
    }

    public static <T> T peekLast(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    public static <T> T removeLast(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.remove(list.size() - 1);
    }
}
