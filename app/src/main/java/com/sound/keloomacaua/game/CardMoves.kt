package com.sound.keloomacaua.game
import com.sound.keloomacaua.game.CardUtils.Companion.cardHasRank
import com.sound.keloomacaua.game.CardUtils.Companion.getCardRank
import com.sound.keloomacaua.game.CardUtils.Companion.getCardSuite
import com.sound.keloomacaua.game.CardUtils.Companion.hasSameRank
import com.sound.keloomacaua.game.CardUtils.Companion.hasSameSuite
import com.sound.keloomacaua.game.CardUtils.Companion.peekLast
import com.sound.keloomacaua.game.CardUtils.Companion.removeLast
import java.util.*
import java.util.function.Consumer

// FIXME: this assumes there are only 2 players
object CardMoves {
    var game: Game = Game()
    var player = 0
    private var challengedPlayer = -1

    // ---------------------
    // ACTIONS -------------
    // ---------------------
    fun deal() {
        game.deckRemainingCards.clear()
        game.playedCards.clear()
        game.playersTurn = 0

        // Create list of all integers from 1 to 54 = number of all cards and randomize their order
        for (i in 0..53) {
            game.deckRemainingCards.add(i)
        }
        game.deckRemainingCards.shuffle()
        for (i in 0 until CARDS_PER_PLAYER) {
            val players: List<Player> = game.players
            players.forEach(Consumer { player: Player -> removeLast(game.deckRemainingCards)?.let { player.cards.add(it) } })
        }
        for (player in game.players) {
            player.cards.sort()
        }

        // Play first card and remove from deck
        removeLast(game.deckRemainingCards)?.let { game.playedCards.add(it) }
    }

    fun playCardAt(cardPosition: Int) {
        val player = game.players[player]
        val card = player.cards[cardPosition]
        val isFourCard: Boolean = cardHasRank(card, CardUtils.CARD_FOUR)
        if (isFourCard) {
            game.activeSkipTurns = game.activeSkipTurns + 1
            // challenge next player to skip turns
            challengedPlayer = getPlayerAfter(this.player)
        }

        //suite override
        if (getCardRank(card).equals(CardUtils.CARD_ACE)) {
            game.playerPicksSuite = this.player
        }
        if (game.suiteOverride.isNotEmpty()) {
            // reset override when card is played
            game.suiteOverride = ""
        }

        // owed cards don't accrue when skipping
        if (game.activeSkipTurns == 0) {
            val cardRank: String = getCardRank(card)
            when (cardRank) {
                CardUtils.CARD_TWO -> game.owedCards += 2
                CardUtils.CARD_THREE -> game.owedCards += 3
                CardUtils.CARD_JOKER -> game.owedCards += 5
            }
        }
        val remainingCards = game.playedCards.size - 1 + game.deckRemainingCards.size
        game.owedCards = Math.min(game.owedCards, remainingCards)
        game.moveStarted = true
        game.playedCards.add(card)
        if (player.cards.size > 1) {
            player.cards.removeAt(cardPosition)
            if (!canMakeAnyMove()) {
                //auto move to next turn if there is no more action possible
                endTurn()
            }
        } else {
            //announce game over
            removeLast(player.cards)
            game.state = GameState.Finished
        }
    }

    fun drawCards() {
        val player = game.players[player]
        val numberOfCards = if (game.owedCards != 0) game.owedCards else 1
        for (i in 0 until numberOfCards) {
            ensureEnoughSpareCards()
            if (game.deckRemainingCards.size > 0) {
                removeLast(game.deckRemainingCards)?.let { player.cards.add(it) }
            }
        }
        player.cards.sort()
        game.owedCards = 0
        endTurn()
    }

    fun getPlayerAfter(playerIndex: Int): Int {
        return (playerIndex + 1) % game.players.size
    }

    fun endTurn() {
        var nextPlayer = getPlayerAfter(player)
        if (game.activeSkipTurns > 0) {
            if (challengedPlayer == nextPlayer) {
                game.playerToSkipTurn = nextPlayer
                challengedPlayer = -1
            } else if (game.playerToSkipTurn == nextPlayer) {
                // already skipping
                game.activeSkipTurns = game.activeSkipTurns - 1
                nextPlayer = getPlayerAfter(nextPlayer)
            }
        } else {
            game.playerToSkipTurn = -1
        }
        game.moveStarted = false
        game.playersTurn = nextPlayer
    }

    fun changeSuite(suite: String?) {
        game.suiteOverride = suite!!
        game.playerPicksSuite = -1
        endTurn()
    }

    // ---------------------
    // CHECKS --------------
    // ---------------------
    val isCurrentPlayer: Boolean
        get() = player == game.playersTurn && topCard != -1

    fun canPickSuite(): Boolean {
        return game.playerPicksSuite == player
    }

    val isAskedToSkip: Boolean
        get() = isCurrentPlayer && game.playerToSkipTurn == player && game.activeSkipTurns > 0

    fun canTakeCards(): Boolean {
        return isCurrentPlayer && !canPickSuite() && !game.moveStarted && !isAskedToSkip
    }

    fun canEndTurn(): Boolean {
        return isCurrentPlayer && !canPickSuite() && (isAskedToSkip || game.moveStarted && canMakeAnyMove())
    }

    fun canPlayAnyCard(): Boolean {
        val cards: List<Int> = game.players[player].cards
        for (card in cards) {
            if (canPlayCard(card)) {
                return true
            }
        }
        return false
    }

    fun canMakeAnyMove(): Boolean {
        return canPlayAnyCard() || canPickSuite()
    }

    fun canPlayCardAt(cardPositionInHand: Int): Boolean {
        return if (game.playersTurn != player) {
            false
        } else {
            if (localPlayerCards().size > 0) {
                canPlayCard(localPlayerCards()[cardPositionInHand])
            } else {
                false
            }
        }
    }

    fun canPlayCard(card: Int): Boolean {
        var canMove = false
        val topCard = topCard
        val isChallengeCard = challengeCards.contains(getCardRank(card))
        val correctChallenge = game.owedCards == 0 || isChallengeCard
        val correctSuite = (game.suiteOverride.isEmpty() && hasSameSuite(topCard, card)
                || !game.suiteOverride.isEmpty() && game.suiteOverride == getCardSuite(card))
        val correctRank: Boolean = hasSameRank(topCard, card)
        val isSpecial =
                specialCards.contains(getCardRank(card)) || specialCards.contains(getCardRank(topCard)) && game.suiteOverride.isEmpty()
        if (game.moveStarted) {
            canMove = correctRank
        } else if (isAskedToSkip) {
            canMove = cardHasRank(card, CardUtils.CARD_FOUR)
        } else if (correctChallenge && (correctRank || correctSuite || isSpecial)) {
            canMove = true
        }
        return canMove
    }

    // ---------------------
    // HELPERS -------------
    // ---------------------
    fun localPlayerCards(): List<Int> {
        val player = game.players[player]
        return player.cards
    }

    private fun ensureEnoughSpareCards() {
        if (game.deckRemainingCards.isEmpty()) {
            // noinspection ConstantConditions
            val lastCard: Int = removeLast(game.playedCards)!!
            game.deckRemainingCards.addAll(game.playedCards)
            game.deckRemainingCards.shuffle()
            game.playedCards.clear()
            game.playedCards.add(lastCard)
        }
    }

    // FIXME: assumes there are only 2 players
    val opponentCardsCount: Int
        get() {
            if (game.players.size < 2) {
                return -1
            }
            val otherPlayerIndex = game.players.size - player - 1
            return game.players[otherPlayerIndex].cards.size
        }

    // FIXME: assumes there are only 2 players
    val opponentName: String
        get() {
            if (game.players.size < 2) {
                return "waiting for other player..."
            }
            val otherPlayerIndex = game.players.size - player - 1
            return game.players[otherPlayerIndex].name
        }

    val topCard: Int
        get() {
            return peekLast(game.playedCards)!!
        }


    @JvmStatic
    private val CARDS_PER_PLAYER = 5

    @JvmStatic
    private val challengeCards = listOf(CardUtils.CARD_TWO, CardUtils.CARD_THREE, CardUtils.CARD_JOKER)

    @JvmStatic
    private val specialCards = listOf(CardUtils.CARD_ACE, CardUtils.CARD_JOKER)


}