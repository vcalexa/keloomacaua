package com.sound.keloomacaua.game

import java.io.Serializable

class Game : Serializable {
    var gameId: Long = System.currentTimeMillis()
    var player1Joined: String? = null
    var player2Joined: String? = null
    var playersTurn = 0
    var player1Cards: List<Int>? = null
    var player2Cards: List<Int>? = null
    var deckRemainingCards: List<Int>? = null
    var playedCards: List<Int>? = null
    var whoWon = -1
}