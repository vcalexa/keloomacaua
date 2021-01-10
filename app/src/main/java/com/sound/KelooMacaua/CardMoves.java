package com.sound.KelooMacaua;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardMoves {
    private Card card = new Card();
    private List<Integer> player1Cards;
    private List<Integer> player2Cards;
    private List<Integer> deckOfCards;
    private List<Integer> cardsPlayed;
    private int playerTurn;

    private static CardMoves single_instance = null;

    private CardMoves() {
        this.deal();
    }

    public static CardMoves getInstance() {
        if (single_instance == null)
            single_instance = new CardMoves();

        return single_instance;
    }

    public void deal() {
        player1Cards = new ArrayList<>();
        player2Cards = new ArrayList<>();
        deckOfCards = new ArrayList<>();
        cardsPlayed = new ArrayList<>();
        playerTurn = 1;

        //Create list of all integers from 1 to 54 = number of all cards and randomize their order
        for (int i = 0; i < 54; i++) {
            deckOfCards.add(i);
        }
        Collections.shuffle(deckOfCards);

        //Add cards to players an remove from deck
        for (int i = 53; i >= 49; i--) {
            player1Cards.add(deckOfCards.get(i));
            player2Cards.add(deckOfCards.get(i - 5));
        }

        for (int i = 53; i >= 44; i--) {
            deckOfCards.remove(i);
        }

        //Play first card and remove from deck
        cardsPlayed.add(deckOfCards.get(deckOfCards.size() - 1));
        deckOfCards.remove(deckOfCards.size() - 1);
    }

    public void player1Move(List<Integer> playedCards, int playerTurn) {
        for (int i = player1Cards.size() - 1; i >= 0; i--) {
            deckOfCards.add(playedCards.get(i));
            player1Cards.remove(i);
        }
    }

    public void player2Move(List<Integer> playedCards) {
        for (int i = player2Cards.size() - 1; i >= 0; i--) {
            deckOfCards.add(playedCards.get(i));
            player2Cards.remove(i);
        }
    }

    public void player1Takes(Integer numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            player1Cards.add(getLast(deckOfCards));
            deckOfCards.remove(deckOfCards.size() - 1);
        }
    }

    public void player2Takes(Integer numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            player2Cards.add(getLast(deckOfCards));
            deckOfCards.remove(deckOfCards.size() - 1);
        }
    }

    private List<Integer> activePlayerCards(int playerTurn) {
        if (playerTurn == 1) {
            return player1Cards;
        }
        else {
            return player2Cards;
        }
    }

    private void changeTurn() {
        if (playerTurn == 1) {
            playerTurn = 2;
        }
        if (playerTurn == 2) {
            playerTurn = 1;
        }
    }

    @SuppressLint("NewApi")
    public boolean canMove(int playerTurn) {
        boolean canMove = false;

        List<Integer> activePlayerCards = new ArrayList<>();
        if (playerTurn == 1) {
            activePlayerCards = player1Cards;
        }

        String[] specialCards = {"Ace", "Joker"};

        for (int playerCard : activePlayerCards) {
            if (card.hasSameRank(getLast(cardsPlayed), playerCard) ||
                    card.hasSameSuite(getLast(cardsPlayed), playerCard) ||
                    Arrays.stream(specialCards).anyMatch(card.getCardRank(playerCard)::equals)) {
                canMove = true;
            }
        }
        return canMove;
    }

    private int getLast(List<Integer> list) {
        return list.get(list.size() - 1);
    }
}