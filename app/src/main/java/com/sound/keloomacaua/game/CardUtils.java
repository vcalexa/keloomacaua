package com.sound.keloomacaua.game;

import android.content.Context;

import androidx.annotation.DrawableRes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private static final String[] ranks = {CARD_ACE, CARD_TWO, CARD_THREE, CARD_FOUR, "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king"};


    public static String getImageViewName(int n) {
        if (n < 0) {
            return "card_back";
        } else {
            return String.format("%s_of_%s", getCardRank(n), getCardSuite(n));
        }
    }

    public static HashMap<Integer, List<String>> cardMap() {
        int i = 0;
        HashMap<Integer, List<String>> cardMap = new HashMap<>();
        for (String rank : ranks) {
            for (String suite : SUITS) {
                cardMap.put(i++, listOf(rank, suite));
            }
        }
        for (String suite : JOKER_SUITS) {
            cardMap.put(i++, listOf(CARD_JOKER, suite));
        }
        return cardMap;
    }

    public static String getCardRank(int cardNumber) {
        return cardMap().get(cardNumber).get(0);
    }

    public static String getCardSuite(int cardNumber) {
        return cardMap().get(cardNumber).get(1);
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

    private static List<String> listOf(String rank, String suite) {
        List<String> cardList = new ArrayList<>();
        cardList.add(rank);
        cardList.add(suite);
        return cardList;
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
