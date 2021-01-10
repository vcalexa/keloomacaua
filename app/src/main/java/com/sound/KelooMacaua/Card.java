package com.sound.KelooMacaua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Card {

    private static String[] suits = {"Hearts", "Spades", "Diamonds", "Clubs"};
    private static String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};


    private static HashMap<Integer, List<String>> cardMap() {
        int i = 0;
        HashMap<Integer, List<String>> cardMap = new HashMap<>();
        for (String suite : suits) {
            for (String rank : ranks) {
                cardMap.put(i, listOf(rank, suite));
                i++;
            }
        }
        cardMap.put(52, listOf("Joker", "Black"));
        cardMap.put(53, listOf("Joker", "Color"));

        return cardMap;
    }

    public String getCardRank(int cardNumber) {
        return cardMap().get(cardNumber).get(0);
    }

    public String getCardSuite(int cardNumber) {
        return cardMap().get(cardNumber).get(1);
    }

    public boolean hasSameSuite(int card1, int card2) {
        return getCardSuite(card1) == getCardSuite(card2);
    }

    public boolean hasSameRank(int card1, int card2) {
        return getCardRank(card1) == getCardRank(card2);
    }

    private static List<String> listOf(String rank, String suite) {
        List<String> cardList = new ArrayList<>();
        cardList.add(rank);
        cardList.add(suite);
        return cardList;
    }
}
