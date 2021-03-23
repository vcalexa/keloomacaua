package com.sound.keloomacaua.game;

import static com.sound.keloomacaua.game.CardUtils.getCardRank;
import static com.sound.keloomacaua.game.CardUtils.getCardSuite;
import static com.sound.keloomacaua.game.CardUtils.hasSameRank;
import static com.sound.keloomacaua.game.CardUtils.hasSameSuite;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// FIXME: this assumes there are only 2 players
public class CardMoves implements Serializable {
    private static final int CARDS_PER_PLAYER = 5;
    private static final String FOUR_CARD = "four";

    private Game game;

    private static CardMoves single_instance = null;
    private int player;
    private boolean skipTurnDone = true;

    public boolean isSkipTurnDone() {
        return skipTurnDone;
    }

    public void setSkipTurnDone(boolean skipTurnDone) {
        this.skipTurnDone = skipTurnDone;
    }

    private CardMoves() {
        game = new Game();
    }

    public static CardMoves getInstance() {
        if (single_instance == null)
            single_instance = new CardMoves();

        return single_instance;
    }

    public void deal() {
        game.getDeckRemainingCards().clear();
        game.getPlayedCards().clear();
        game.setPlayersTurn(1);

        //Create list of all integers from 1 to 54 = number of all cards and randomize their order
        for (int i = 0; i < 54; i++) {
            game.getDeckRemainingCards().add(i);
        }
        Collections.shuffle(game.getDeckRemainingCards());

        for (int i = 0; i < CARDS_PER_PLAYER; i++) {
            List<Player> players = game.getPlayers();
            players.forEach(player -> player.getCards().add(removeLast(game.getDeckRemainingCards())));
        }

        //Play first card and remove from deck
        game.getPlayedCards().add(removeLast(game.getDeckRemainingCards()));
    }

    public void playCard(int cardIndex) {

        if (getCardRank(cardIndex).equals(FOUR_CARD)) {
            setSkipTurnDone(false);
            System.out.println("Card 4 was played!!!");
            changeTurn();
        }

        Player player = game.getPlayers().get(this.player);
        int card = player.getCards().get(cardIndex);

        //suite override
        if (getCardRank(card).equals("ace")) {
            game.playerPicksSuite = this.player;
        }
        if (!game.suiteOverride.isEmpty()) {
            //reset override when card is played
            game.suiteOverride = "";
        }

        //owed cards
        String cardRank = getCardRank(card);
        switch (cardRank) {
            case "two":
                game.owedCards += 2;
                break;
            case "three":
                game.owedCards += 3;
                break;
            case "joker":
                game.owedCards += 5;
                break;
        }

        game.moveStarted = true;

        game.getPlayedCards().add(card);
        if (player.getCards().size() > 1) {
            player.getCards().remove(cardIndex);
            if (!canMakeAnyMove()) {
                //auto move to next turn if there is no more action possible
                changeTurn();
            }
        } else {
            //announce game over
            removeLast(player.getCards());
            game.setWhoWon(getPlayerTurn());
            game.setState(GameState.Finished);
        }
    }

    public void pickCards() {
        Player player = game.getPlayers().get(this.player);
        int numberOfCards = game.owedCards != 0 ? game.owedCards : 1;
        for (int i = 0; i < numberOfCards; i++) {
            ensureEnoughSpareCards();
            player.getCards().add(removeLast(game.getDeckRemainingCards()));
        }
        game.owedCards = 0;
        changeTurn();
    }

    public List<Integer> localPlayerCards() {
        Player player = game.getPlayers().get(this.player);
        return player.getCards();
    }

    public boolean isGameOver() {
        for (Player value : game.getPlayers()) {
            if (value.getCards().size() == 0) {
                return true;
            }
        }
        return false;
    }

    public void changeTurn() {
        int numPlayers = game.getPlayers().size();
        int currentPlayer = game.getPlayersTurn();
        int nextPlayer = (currentPlayer + 1) % numPlayers;
        game.setPlayersTurn(nextPlayer);
        if (shouldSkipTurn(getTopCard())) setSkipTurnDone(true);
        game.moveStarted = false;
    }

    public void changeSuite(String suite) {
        game.suiteOverride = suite;
        game.playerPicksSuite = -1;
        changeTurn();
    }

    static final List<String> specialCards = Arrays.asList("ace", "joker");

    public boolean canMakeAnyMove() {
        List<Integer> cards = game.getPlayers().get(player).getCards();
        System.out.println("\n\n\n---topCard=" + getCardRank(getTopCard()) + " of " + getCardSuite(getTopCard()));
        System.out.println("\n\n\n---pickSuite=" + game.playerPicksSuite);
        for (int card : cards) {
            if (isMovePossible(card)) {
                return true;
            }
        }
        if (game.playerPicksSuite != -1) {
            return true;
        }
        return false;
    }

    public boolean isMovePossible(int cardNumber) {
        boolean canMove = false;

        int topCard = getTopCard();

        boolean isChallengeCard = Arrays.asList("two", "three", "joker").contains(getCardRank(cardNumber));
        boolean correctChallenge = game.owedCards == 0 || isChallengeCard;
        boolean correctSuite = (game.suiteOverride.isEmpty() && hasSameSuite(topCard, cardNumber))
                || (!game.suiteOverride.isEmpty() && game.suiteOverride.equals(getCardSuite(cardNumber)));
        boolean correctRank = hasSameRank(topCard, cardNumber);
        boolean isSpecial = specialCards.contains(getCardRank(cardNumber)) || (specialCards.contains(getCardRank(topCard)) && game.suiteOverride.isEmpty());

        if (game.moveStarted) {
            canMove = correctRank;
        } else if (shouldSkipTurn(topCard)) {
            canMove = false;
        } else if (correctChallenge && (correctRank || correctSuite || isSpecial)) {
            canMove = true;
        }
        String card = "\n" + getCardRank(cardNumber) + " of " + getCardSuite(cardNumber);
        System.out.println(card + " " + canMove);
        System.out.println(card + " isChallenge=" + isChallengeCard);
        System.out.println(card + " owedCards=" + game.owedCards);
        System.out.println(card + " correctChallenge=" + isChallengeCard);
        System.out.println(card + " correctSuite=" + correctSuite);
        System.out.println(card + " correctRank=" + correctRank);
        System.out.println(card + " isSpecial=" + isSpecial);

        return canMove;
    }

    private boolean shouldSkipTurn(int topCard) {
        boolean skip = false;
        if (getCardRank(topCard).equals(FOUR_CARD) && !hasFourCard() && !skipTurnDone) {
            skip = true;
        }
        return skip;
    }

    private boolean hasFourCard() {
        return Arrays.asList(localPlayerCards()).contains(FOUR_CARD);
    }


    public boolean hasMoved(int position) {
        if (getPlayerTurn() == player && isMovePossible(localPlayerCards().get(position))) {
            playCard(position);
            return true;
        } else {
            return false;
        }
    }

    private void ensureEnoughSpareCards() {
        if (game.getDeckRemainingCards().isEmpty()) {
            //noinspection ConstantConditions
            int lastCard = removeLast(game.getPlayedCards());

            game.getDeckRemainingCards().addAll(game.getPlayedCards());
            Collections.shuffle(game.getDeckRemainingCards());

            game.getPlayedCards().clear();
            game.getPlayedCards().add(lastCard);
        }
    }

    // FIXME: assumes there are only 2 players
    public int getOpponentCardsCount() {
        if (game.getPlayers().size() < 2) {
            return -1;
        }
        int otherPlayerIndex = (game.getPlayers().size() - player - 1);
        return game.getPlayers().get(otherPlayerIndex).getCards().size();
    }

    public int getTopCard() {
        Integer lastCard = peekLast(game.getPlayedCards());
        return lastCard != null ? lastCard : -1;
    }

    private <T> T peekLast(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(list.size() - 1);
    }

    private <T> T removeLast(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.remove(list.size() - 1);
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

    public int getPlayer() {
        return this.player;
    }
}