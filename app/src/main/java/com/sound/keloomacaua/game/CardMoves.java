package com.sound.keloomacaua.game;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardMoves implements Serializable {
    private long gameId;
    private CardUtils cardUtils = new CardUtils();
    private List<Integer> player1Cards;
    private List<Integer> player2Cards;
    private List<Integer> deckOfCards;
    private List<Integer> cardsPlayed;
    private int playerTurn;

    private static CardMoves single_instance = null;

    private CardMoves() {
        this.deal();
    }

    private CardMoves(boolean toJoin) {

    }

    public static CardMoves getInstance() {
        if (single_instance == null)
            single_instance = new CardMoves();

        return single_instance;
    }

    public static CardMoves getEmptyInstance() {
        if (single_instance == null)
            single_instance = new CardMoves(true);

        return single_instance;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public CardUtils getCardUtils() {
        return single_instance.cardUtils;
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

        deckOfCards.subList(44, 54).clear();

        //Play first card and remove from deck
        cardsPlayed.add(deckOfCards.get(deckOfCards.size() - 1));
        deckOfCards.remove(deckOfCards.size() - 1);
    }

    public void player1Move(List<Integer> playedCards, int playerTurn) {
        for (int i = player1Cards.size() - 1; i >= 0; i--) {
            cardsPlayed.add(playedCards.get(i));
            player1Cards.remove(i);
        }
    }

    public void player2Move(List<Integer> playedCards) {
        for (int i = player2Cards.size() - 1; i >= 0; i--) {
            cardsPlayed.add(playedCards.get(i));
            player2Cards.remove(i);
        }
    }

    public void player1Takes(Integer numberOfCards) {
        if (!deckOfCards.isEmpty()) {
            for (int i = 0; i < numberOfCards; i++) {
                player1Cards.add(getLast(deckOfCards));
                deckOfCards.remove(deckOfCards.size() - 1);
            }
        }

        turnPlayerCardsOver();
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
        } else {
            return player2Cards;
        }
    }

    public void changeTurn() {
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

        String[] specialCards = {"ace", "joker"};

        for (int playerCard : activePlayerCards) {
            if (cardUtils.hasSameRank(getLast(cardsPlayed), playerCard) ||
                    cardUtils.hasSameSuite(getLast(cardsPlayed), playerCard) ||
                    Arrays.asList(specialCards).contains(cardUtils.getCardRank(playerCard))) {
                canMove = true;
            }
        }
        return canMove;

    }

    @SuppressLint("NewApi")
    public boolean playIfPossible(int cardNumber) {
        boolean canMove = false;

        String[] specialCards = {"ace", "joker"};

        if (cardUtils.hasSameRank(getLast(cardsPlayed), cardNumber) ||
                cardUtils.hasSameSuite(getLast(cardsPlayed), cardNumber) ||
                Arrays.asList(specialCards).contains(cardUtils.getCardRank(cardNumber))) {
            cardsPlayed.add(cardNumber);

            //player1Move(toPlay, 1);
            canMove = true;

            turnPlayerCardsOver();
        }


        return canMove;
    }

    private void turnPlayerCardsOver() {
        if (deckOfCards.isEmpty()) {
            int lastCard = getLast();

            deckOfCards = new ArrayList<>(cardsPlayed);
            deckOfCards.remove(deckOfCards.size() - 1);

            cardsPlayed = new ArrayList<>();
            cardsPlayed.add(lastCard);
            Collections.shuffle(deckOfCards);
        }
    }

    private int getLast(List<Integer> list) {
        return list.get(list.size() - 1);
    }

    public int getLast() {
        return getLast(cardsPlayed);
    }

    public List<Integer> getPlayer1Cards() {
        return player1Cards;
    }

    public void setPlayer1Cards(List<Integer> player1Cards) {
        this.player1Cards = player1Cards;
    }

    public List<Integer> getPlayer2Cards() {
        return player2Cards;
    }

    public void setPlayer2Cards(List<Integer> player2Cards) {
        this.player2Cards = player2Cards;
    }

    public List<Integer> getDeckOfCards() {
        return deckOfCards;
    }

    public void setDeckOfCards(List<Integer> deckOfCards) {
        this.deckOfCards = deckOfCards;
    }

    public List<Integer> getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(List<Integer> cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }
}