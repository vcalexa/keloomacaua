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
    private int challengedPlayer = -1;

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

        // Create list of all integers from 1 to 54 = number of all cards and randomize their order
        for (int i = 0; i < 54; i++) {
            game.getDeckRemainingCards().add(i);
        }
        Collections.shuffle(game.getDeckRemainingCards());

        for (int i = 0; i < CARDS_PER_PLAYER; i++) {
            List<Player> players = game.getPlayers();
            players.forEach(player -> player.getCards().add(removeLast(game.getDeckRemainingCards())));
        }
        for (Player player : game.getPlayers()) {
            Collections.sort(player.getCards());
        }

        // Play first card and remove from deck
        game.getPlayedCards().add(removeLast(game.getDeckRemainingCards()));
    }

    public void playCardAt(int cardPosition) {
        Player player = game.getPlayers().get(this.localPlayerIndex);
        int card = player.getCards().get(cardPosition);

        boolean isFourCard = cardHasRank(card, CARD_FOUR);

        if (isFourCard) {
            game.setActiveSkipTurns(game.getActiveSkipTurns() + 1);
            // challenge next player to skip turns
            this.challengedPlayer = getPlayerAfter(localPlayerIndex);
        }

        //suite override
        if (getCardRank(card).equals(CARD_ACE)) {
            game.playerPicksSuite = this.localPlayerIndex;
        }
        if (!game.suiteOverride.isEmpty()) {
            // reset override when card is played
            game.suiteOverride = "";
        }

        // owed cards don't accrue when skipping
        if (game.getActiveSkipTurns() == 0) {
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
            game.setState(GameState.Finished);
        }
    }

    public void drawCards() {
        Player player = game.getPlayers().get(this.localPlayerIndex);
        int numberOfCards = game.owedCards != 0 ? game.owedCards : 1;
        for (int i = 0; i < numberOfCards; i++) {
            ensureEnoughSpareCards();
            player.getCards().add(removeLast(game.getDeckRemainingCards()));
        }
        Collections.sort(player.getCards());
        game.owedCards = 0;
        endTurn();
    }

    public int getPlayerAfter(int playerIndex) {
        return (playerIndex + 1) % game.getPlayers().size();
    }

    public void endTurn() {
        int nextPlayer = getPlayerAfter(localPlayerIndex);

        if (game.getActiveSkipTurns() > 0) {
            if (this.challengedPlayer == nextPlayer) {
                game.setPlayerToSkipTurn(nextPlayer);
                this.challengedPlayer = -1;
            } else if (game.getPlayerToSkipTurn() == nextPlayer) {
                // already skipping
                game.setActiveSkipTurns(game.getActiveSkipTurns() - 1);
                nextPlayer = getPlayerAfter(nextPlayer);
            }
        } else {
            game.setPlayerToSkipTurn(-1);
        }

        game.moveStarted = false;
        game.setPlayersTurn(nextPlayer);
    }

    public void changeSuite(String suite) {
        game.suiteOverride = suite;
        game.playerPicksSuite = -1;
        endTurn();
    }

    // ---------------------
    // CHECKS --------------
    // ---------------------

    public boolean isCurrentPlayer() {
        return (localPlayerIndex == game.getPlayersTurn() && getTopCard() != -1);
    }

    public boolean canPickSuite() {
        return game.playerPicksSuite == getPlayer();
    }

    public boolean isAskedToSkip() {
        return isCurrentPlayer() && game.getPlayerToSkipTurn() == localPlayerIndex && game.getActiveSkipTurns() > 0;
    }

    public boolean canTakeCards() {
        return isCurrentPlayer() && !canPickSuite() && !game.moveStarted && !isAskedToSkip();
    }

    public boolean canEndTurn() {
        return isCurrentPlayer() && !canPickSuite() && (isAskedToSkip() || (game.moveStarted && canMakeAnyMove()));
    }

    public boolean canPlayAnyCard() {
        List<Integer> cards = game.getPlayers().get(localPlayerIndex).getCards();
        for (int card : cards) {
            if (canPlayCard(card)) {
                return true;
            }
        }
        return false;
    }

    public boolean canMakeAnyMove() {
        return canPlayAnyCard() || canPickSuite();
    }

    public boolean canPlayCardAt(int cardPositionInHand) {
        if (getPlayerTurn() != localPlayerIndex) {
            return false;
        } else {
            if (localPlayerCards().size() > 0) {
                return canPlayCard(localPlayerCards().get(cardPositionInHand));
            } else {
                return false;
            }
        }
    }

    public boolean canPlayCard(int card) {
        boolean canMove = false;

        int topCard = getTopCard();

        boolean isChallengeCard = challengeCards.contains(getCardRank(card));
        boolean correctChallenge = (game.owedCards == 0 || isChallengeCard);
        boolean correctSuite = (game.suiteOverride.isEmpty() && hasSameSuite(topCard, card))
                || (!game.suiteOverride.isEmpty() && game.suiteOverride.equals(getCardSuite(card)));
        boolean correctRank = hasSameRank(topCard, card);
        boolean isSpecial = specialCards.contains(getCardRank(card)) || (specialCards.contains(getCardRank(topCard)) && game.suiteOverride.isEmpty());

        if (game.moveStarted) {
            canMove = correctRank;
        } else if (isAskedToSkip()) {
            canMove = cardHasRank(card, CARD_FOUR);
        } else if (correctChallenge && (correctRank || correctSuite || isSpecial)) {
            canMove = true;
        }

        return canMove;
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
            // noinspection ConstantConditions
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

    // FIXME: assumes there are only 2 players
    public String getOpponentName() {
        if (game.getPlayers().size() < 2) {
            return "waiting for other player...";
        }
        int otherPlayerIndex = (game.getPlayers().size() - localPlayerIndex - 1);
        return game.getPlayers().get(otherPlayerIndex).getName();
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