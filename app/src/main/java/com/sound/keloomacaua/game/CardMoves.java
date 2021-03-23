package com.sound.keloomacaua.game;

import static com.sound.keloomacaua.game.CardUtils.CARD_ACE;
import static com.sound.keloomacaua.game.CardUtils.CARD_FOUR;
import static com.sound.keloomacaua.game.CardUtils.CARD_JOKER;
import static com.sound.keloomacaua.game.CardUtils.CARD_THREE;
import static com.sound.keloomacaua.game.CardUtils.CARD_TWO;
import static com.sound.keloomacaua.game.CardUtils.cardHasRank;
import static com.sound.keloomacaua.game.CardUtils.getCardRank;
import static com.sound.keloomacaua.game.CardUtils.getCardSuite;
import static com.sound.keloomacaua.game.CardUtils.hasSameRank;
import static com.sound.keloomacaua.game.CardUtils.hasSameSuite;
import static com.sound.keloomacaua.game.CardUtils.peekLast;
import static com.sound.keloomacaua.game.CardUtils.removeLast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// FIXME: this assumes there are only 2 players
public class CardMoves {
    private static final int CARDS_PER_PLAYER = 5;
    private static final List<String> challengeCards = Arrays.asList(CARD_TWO, CARD_THREE, CARD_JOKER);
    private static final List<String> specialCards = Arrays.asList(CARD_ACE, CARD_JOKER);

    private Game game;

    private static CardMoves single_instance = null;
    private int localPlayerIndex;
    private boolean skipTurnDone = true;

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

    // ---------------------
    // ACTIONS -------------
    // ---------------------

    public void deal() {
        game.getDeckRemainingCards().clear();
        game.getPlayedCards().clear();
        game.setPlayersTurn(0);

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

    public void playCardAt(int cardPosition) {
        Player player = game.getPlayers().get(this.localPlayerIndex);
        int card = player.getCards().get(cardPosition);

        if (cardHasRank(card, CARD_FOUR)) {
            setSkipTurnDone(false);
            System.out.println("Card 4 was played!!!");
            endTurn();
        }

        //suite override
        if (getCardRank(card).equals(CARD_ACE)) {
            game.playerPicksSuite = this.localPlayerIndex;
        }
        if (!game.suiteOverride.isEmpty()) {
            //reset override when card is played
            game.suiteOverride = "";
        }

        //owed cards
        String cardRank = getCardRank(card);
        switch (cardRank) {
            case CARD_TWO:
                game.owedCards += 2;
                break;
            case CARD_THREE:
                game.owedCards += 3;
                break;
            case CARD_JOKER:
                game.owedCards += 5;
                break;
        }

        game.moveStarted = true;

        game.getPlayedCards().add(card);
        if (player.getCards().size() > 1) {
            player.getCards().remove(cardPosition);
            if (!canMakeAnyMove()) {
                //auto move to next turn if there is no more action possible
                endTurn();
            }
        } else {
            //announce game over
            removeLast(player.getCards());
            // System.out.println("gameOver detected winner index is " + game.getPlayersTurn());
            game.setState(GameState.Finished);
        }
    }

    public void takeOwedCards() {
        Player player = game.getPlayers().get(this.localPlayerIndex);
        int numberOfCards = game.owedCards != 0 ? game.owedCards : 1;
        for (int i = 0; i < numberOfCards; i++) {
            ensureEnoughSpareCards();
            player.getCards().add(removeLast(game.getDeckRemainingCards()));
        }
        game.owedCards = 0;
        endTurn();
    }

    public void endTurn() {
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
        endTurn();
    }

    // ---------------------
    // CHECKS --------------
    // ---------------------

    public boolean canMakeAnyMove() {
        List<Integer> cards = game.getPlayers().get(localPlayerIndex).getCards();
        System.out.println("\n\n\n---topCard=" + getCardRank(getTopCard()) + " of " + getCardSuite(getTopCard()));
        System.out.println("\n\n\n---pickSuite=" + game.playerPicksSuite);
        for (int card : cards) {
            if (canPlayCard(card)) {
                return true;
            }
        }
        return game.playerPicksSuite != -1;
    }

    public boolean canPlayCardAt(int cardPositionInHand) {
        if (getPlayerTurn() != localPlayerIndex) {
            return false;
        } else {
            return canPlayCard(localPlayerCards().get(cardPositionInHand));
        }
    }

    public boolean canPlayCard(int card) {
        boolean canMove = false;

        int topCard = getTopCard();

        boolean isChallengeCard = challengeCards.contains(getCardRank(card));
        boolean correctChallenge = game.owedCards == 0 || isChallengeCard;
        boolean correctSuite = (game.suiteOverride.isEmpty() && hasSameSuite(topCard, card))
                || (!game.suiteOverride.isEmpty() && game.suiteOverride.equals(getCardSuite(card)));
        boolean correctRank = hasSameRank(topCard, card);
        boolean isSpecial = specialCards.contains(getCardRank(card)) || (specialCards.contains(getCardRank(topCard)) && game.suiteOverride.isEmpty());

        if (game.moveStarted) {
            canMove = correctRank;
        } else if (shouldSkipTurn(topCard)) {
            //noinspection ConstantConditions
            canMove = false;
        } else if (correctChallenge && (correctRank || correctSuite || isSpecial)) {
            canMove = true;
        }
//        String cardName = "\n" + getCardRank(card) + " of " + getCardSuite(card);
//        System.out.println(cardName + " " + canMove);
//        System.out.println(cardName + " isChallenge=" + isChallengeCard);
//        System.out.println(cardName + " owedCards=" + game.owedCards);
//        System.out.println(cardName + " correctChallenge=" + isChallengeCard);
//        System.out.println(cardName + " correctSuite=" + correctSuite);
//        System.out.println(cardName + " correctRank=" + correctRank);
//        System.out.println(cardName + " isSpecial=" + isSpecial);

        return canMove;
    }

    private boolean shouldSkipTurn(int topCard) {
        boolean skip = false;
        if (cardHasRank(topCard, CARD_FOUR) && !hasFourCard() && !skipTurnDone) {
            skip = true;
        }
        return skip;
    }

    private boolean hasFourCard() {
        return localPlayerCards().stream().anyMatch(card -> cardHasRank(card, CARD_FOUR));
    }

    // ---------------------
    // HELPERS -------------
    // ---------------------

    public List<Integer> localPlayerCards() {
        Player player = game.getPlayers().get(this.localPlayerIndex);
        return player.getCards();
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
        int otherPlayerIndex = (game.getPlayers().size() - localPlayerIndex - 1);
        return game.getPlayers().get(otherPlayerIndex).getCards().size();
    }

    public int getTopCard() {
        Integer lastCard = peekLast(game.getPlayedCards());
        return lastCard != null ? lastCard : -1;
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

    public void setPlayer(int localPlayerIndex) {
        this.localPlayerIndex = localPlayerIndex;
    }

    public int getPlayer() {
        return this.localPlayerIndex;
    }
}