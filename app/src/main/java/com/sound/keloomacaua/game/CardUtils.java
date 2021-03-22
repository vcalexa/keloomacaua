package com.sound.keloomacaua.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CardUtils implements Serializable {

    private static String[] suits = {"hearts", "spades", "diamonds", "clubs"};
    private static String[] ranks = {"ace", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king"};

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
        for (String suite : suits) {
            for (String rank : ranks) {
                cardMap.put(i, listOf(rank, suite));
                i++;
            }
        }
        cardMap.put(52, listOf("joker", "black"));
        cardMap.put(53, listOf("joker", "color"));

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

    private static List<String> listOf(String rank, String suite) {
        List<String> cardList = new ArrayList<>();
        cardList.add(rank);
        cardList.add(suite);
        return cardList;
    }

}
