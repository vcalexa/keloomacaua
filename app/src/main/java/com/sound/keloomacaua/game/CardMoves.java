package com.sound.keloomacaua.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// FIXME: this assumes there are only 2 players
public class CardMoves implements Serializable {
    private final CardUtils cardUtils = new CardUtils();
    private Game game;

    private static CardMoves single_instance = null;
    private int player;
    private boolean skipTurnDone = true;

    private CardMoves() {
        game = new Game();
    }

    private CardMoves(boolean toJoin) {

    }

    public void createNewGame() {
        this.game = new Game();
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
        if (cardUtils.getCardRank(index).equals("four")) skipTurnDone = false;

        game.getPlayedCards().add(game.getPlayer1Cards().get(index));
        if (game.getPlayer1Cards().size() == 1) {
            // do nothing for now
        } else {
            game.getPlayer1Cards().remove(index);
        }
    }

    public void player2Move(int index) {
        if (cardUtils.getCardRank(index).equals("four")) skipTurnDone = false;

        game.getPlayedCards().add(game.getPlayer2Cards().get(index));
        if (game.getPlayer2Cards().size() == 1) {
            // do nothing for now
        } else {
            game.getPlayer2Cards().remove(index);
        }
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
        if (!game.getDeckRemainingCards().isEmpty()) {
            for (int i = 0; i < numberOfCards; i++) {
                game.getPlayer2Cards().add(getLast(game.getDeckRemainingCards()));
                game.getDeckRemainingCards().remove(game.getDeckRemainingCards().size() - 1);
            }
        }

        turnPlayerCardsOver();
    }

    public List<Integer> localPlayerCards() {
        if (player == 1) {
            return game.getPlayer1Cards();
        } else {
            return game.getPlayer2Cards();
        }
    }

    public boolean isGameOver() {
        boolean gameOver = ((game.getPlayer1Cards().size() == 0) || (game.getPlayer2Cards().size() == 0)) ? true : false;
        return gameOver;
    }

    public void changeTurn() {
        game.setPlayersTurn(3 - game.getPlayersTurn());
    }

    public boolean isMovePossible(int cardNumber) {
        boolean canMove = false;

        String[] specialCards = {"ace", "joker"};
        int topCard = getLast();

        if ((cardUtils.hasSameRank(topCard, cardNumber) ||
                cardUtils.hasSameSuite(topCard, cardNumber) ||
                Arrays.asList(specialCards).contains(cardUtils.getCardRank(cardNumber)) ||
                Arrays.asList(specialCards).contains(cardUtils.getCardRank(topCard))) &&
                !isSkipTurn(cardNumber, topCard))
        // OR (top card is not 4 OR (top card is 4 AND cardNUmber == 4))
        {
            canMove = true;
        }

        return canMove;
    }

    private boolean isSkipTurn(int cardNumber, int topCard) {
        boolean skip = false;
        if ((cardUtils.getCardRank(topCard).equals("four")) && (!cardUtils.getCardRank(cardNumber).equals("four") && skipTurnDone == false)) {
            skip = true;
            skipTurnDone = true;
        }
        return skip;
    }


    public boolean hasMoved(int position) {
        if (getPlayerTurn() == player && isMovePossible(localPlayerCards().get(position))) {
            if (player == 1) {
                player1Move(position);
            } else {
                player2Move(position);
            }
            return true;
        } else {
            return false;
        }
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

    public Integer getOpponentCardsCount() {
        //if(getPlayerTurn() ==1)
        return game.getPlayer1Cards().size();
        //else return game.getPlayer2Cards().size();
    }

    private int getLast(List<Integer> list) {
        return list.get(list.size() - 1);
    }

    public int getLast() {
        return getLast(game.getPlayedCards());
    }

    private int getPlayerTurn() {
        return game.getPlayersTurn();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(int player) {
        this.player = player;
    }
}