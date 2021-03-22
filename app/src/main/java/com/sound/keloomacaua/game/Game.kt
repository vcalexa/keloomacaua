package com.sound.keloomacaua.game

import java.io.Serializable

class Game : Serializable {
    var gameId: Long = System.currentTimeMillis()
    var players: MutableList<Player> = mutableListOf()
    var playersTurn = 0
    var deckRemainingCards: MutableList<Int> = mutableListOf()
    var playedCards: MutableList<Int> = mutableListOf()
    var whoWon = -1

    var state: GameState = GameState.Waiting

    fun hasPlayer(playerId: String): Int {
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