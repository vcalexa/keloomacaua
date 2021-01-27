package com.sound.keloomacaua.game;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Game implements Serializable {
    private Date dateTime;
    private long gameId;
    private String player1Joined;
    private String player2Joined;
    private int playersTurn;
    private List<Integer> player1Cards;
    private List<Integer> player2Cards;
    private List<Integer> deckRemainingCards;
    private List<Integer> playedCards;
    private int whoWon;

    public Game() {
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getPlayer1Joined() {
        return player1Joined;
    }

    public void setPlayer1Joined(String player1Joined) {
        this.player1Joined = player1Joined;
    }

    public String getPlayer2Joined() {
        return player2Joined;
    }

    public void setPlayer2Joined(String player2Joined) {
        this.player2Joined = player2Joined;
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

    public List<Integer> getDeckRemainingCards() {
        return deckRemainingCards;
    }

    public void setDeckRemainingCards(List<Integer> deckRemainingCards) {
        this.deckRemainingCards = deckRemainingCards;
    }

    public List<Integer> getPlayedCards() {
        return playedCards;
    }

    public void setPlayedCards(List<Integer> playedCards) {
        this.playedCards = playedCards;
    }

    public int getWhoWon() {
        return whoWon;
    }

    public void setWhoWon(int whoWon) {
        this.whoWon = whoWon;
    }

    public int getPlayersTurn() {
        return playersTurn;
    }

    public void setPlayersTurn(int playersTurn) {
        this.playersTurn = playersTurn;
    }


}
