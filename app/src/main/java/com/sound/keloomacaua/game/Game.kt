package com.sound.keloomacaua.game

import java.io.Serializable

class Game : Serializable {
    @JvmField
    var suiteOverride: String = ""

    @JvmField
    var playerPicksSuite: Int = -1

    @JvmField
    var moveStarted: Boolean = false

    @JvmField
    var owedCards: Int = 0

    var gameId: Long = System.currentTimeMillis()
    var players: MutableList<Player> = mutableListOf()
    var playersTurn = 0
    var deckRemainingCards: MutableList<Int> = mutableListOf()
    var playedCards: MutableList<Int> = mutableListOf()

    var state: GameState = GameState.Waiting

    fun findPlayer(playerId: String): Int {
        players.forEachIndexed { index, player ->
            if (player.id == playerId) {
                return index
            }
        }
        return -1;
    }
}

enum class GameState : Serializable {
    Waiting,
    Started,
    Finished,
}