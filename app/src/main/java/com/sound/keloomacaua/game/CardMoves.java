package com.sound.keloomacaua.game;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardMoves implements Serializable {
    private CardUtils cardUtils = new CardUtils();
    private Game game;
    public DatabaseReference mdataRef;

    private static CardMoves single_instance = null;

    private CardMoves() {
        game = new Game();
        mdataRef = FirebaseDatabase.getInstance().getReference();
    }

    private CardMoves(boolean toJoin) {

    }

    public void createNewGame(){
        game = new Game();
        this.deal();
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
        return game.getGameId();
    }

    public void setGameId(long gameId) {
        this.game.setGameId(gameId);
    }

    public CardUtils getCardUtils() {
        return single_instance.cardUtils;
    }

    public void deal() {
        game.setPlayer1Cards(new ArrayList<>());
        game.setPlayer2Cards(new ArrayList<>());
        game.setDeckRemainingCards(new ArrayList<>());
        game.setPlayedCards(new ArrayList<>());
        game.setPlayersTurn(1);

        //Create list of all integers from 1 to 54 = number of all cards and randomize their order
        for (int i = 0; i < 54; i++) {
            game.getDeckRemainingCards().add(i);
        }
        Collections.shuffle(game.getDeckRemainingCards());

        //Add cards to players an remove from deck
        for (int i = 53; i >= 49; i--) {
            game.getPlayer1Cards().add(game.getDeckRemainingCards().get(i));
            game.getPlayer2Cards().add(game.getDeckRemainingCards().get(i - 5));
        }

        game.getDeckRemainingCards().subList(44, 54).clear();

        //Play first card and remove from deck
        game.getPlayedCards().add(game.getDeckRemainingCards().get(game.getDeckRemainingCards().size() - 1));
        game.getDeckRemainingCards().remove(game.getDeckRemainingCards().size() - 1);
    }


    public void player1Move(int index) {
        game.getPlayedCards().add(game.getPlayer1Cards().get(index));
        game.getPlayer1Cards().remove(index);
        mdataRef.child("games").child(String.valueOf(game.getGameId())).child("player1Cards").setValue(game.getPlayer1Cards());
    }

    public void player2Move(int index) {
        game.getPlayedCards().add(game.getPlayer1Cards().get(index));
        game.getPlayer1Cards().remove(index);
        mdataRef.child("games").child(String.valueOf(game.getGameId())).child("player2Cards").setValue(game.getPlayer2Cards());
    }

    public void player1Takes(Integer numberOfCards) {
        if (!game.getDeckRemainingCards().isEmpty()) {
            for (int i = 0; i < numberOfCards; i++) {
                game.getPlayer1Cards().add(getLast(game.getDeckRemainingCards()));
                game.getDeckRemainingCards().remove(game.getDeckRemainingCards().size() - 1);
            }
        }

        turnPlayerCardsOver();
    }

    public void player2Takes(Integer numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            game.getPlayer2Cards().add(getLast(game.getDeckRemainingCards()));
            game.getDeckRemainingCards().remove(game.getDeckRemainingCards().size() - 1);
        }
    }

    private List<Integer> activePlayerCards(int playerTurn) {
        if (playerTurn == 1) {
            return game.getPlayer1Cards();
        } else {
            return game.getPlayer2Cards();
        }
    }

    public void changeTurn() {
        if (game.getPlayersTurn() == 1) {
            setPlayerTurn(2);
            mdataRef.child("games").child(String.valueOf(game.getGameId())).child("playersTurn").setValue(2);
        } else if (getPlayerTurn() == 2) {
            setPlayerTurn(1);
            mdataRef.child("games").child(String.valueOf(game.getGameId())).child("playersTurn").setValue(1);
        }
    }

    @SuppressLint("NewApi")
    public boolean canMove(int playerTurn) {
        boolean canMove = false;

        List<Integer> activePlayerCards = new ArrayList<>();
        if (playerTurn == 1) {
            activePlayerCards = game.getPlayer1Cards();
        }

        String[] specialCards = {"ace", "joker"};

        for (int playerCard : activePlayerCards) {
            if (cardUtils.hasSameRank(getLast(game.getPlayedCards()), playerCard) ||
                    cardUtils.hasSameSuite(getLast(game.getPlayedCards()), playerCard) ||
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

        if (cardUtils.hasSameRank(getLast(game.getPlayedCards()), cardNumber) ||
                cardUtils.hasSameSuite(getLast(game.getPlayedCards()), cardNumber) ||
                Arrays.asList(specialCards).contains(cardUtils.getCardRank(cardNumber))) {
            game.getPlayedCards().add(cardNumber);

            //player1Move(toPlay, 1);
            canMove = true;

            turnPlayerCardsOver();
        }


        return canMove;
    }

    private void turnPlayerCardsOver() {
        if (game.getDeckRemainingCards().isEmpty()) {
            int lastCard = getLast();

            game.setDeckRemainingCards(new ArrayList<>(game.getPlayedCards()));
            game.getDeckRemainingCards().remove(game.getDeckRemainingCards().size() - 1);

            game.setPlayedCards(new ArrayList<>());
            game.getPlayedCards().add(lastCard);
            Collections.shuffle(game.getDeckRemainingCards());
        }
    }

    private int getLast(List<Integer> list) {
        return list.get(list.size() - 1);
    }

    public int getLast() {
        return getLast(game.getPlayedCards());
    }

    public List<Integer> getPlayer1Cards() {
        return game.getPlayer1Cards();
    }

    public void setPlayer1Cards(List<Integer> player1Cards) {
        this.game.setPlayer1Cards(player1Cards);
    }

    public List<Integer> getPlayer2Cards() {
        return game.getPlayer2Cards();
    }

    public void setPlayer2Cards(List<Integer> player2Cards) {
        this.game.setPlayer1Cards(player2Cards);
    }

    public List<Integer> getDeckOfCards() {
        return game.getDeckRemainingCards();
    }

    public void setDeckOfCards(List<Integer> deckRemainingCards) {
        this.game.setPlayer1Cards(deckRemainingCards);
    }

    public List<Integer> getCardsPlayed() {
        return game.getPlayedCards();
    }

    public void setCardsPlayed(List<Integer> cardsPlayed) {
        this.game.setPlayedCards(cardsPlayed);
    }

    public int getPlayerTurn() {
        return game.getPlayersTurn();
    }

    public void setPlayerTurn(int playerTurn) {
        this.game.setPlayersTurn(playerTurn);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}