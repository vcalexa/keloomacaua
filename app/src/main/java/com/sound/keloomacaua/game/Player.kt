package com.sound.keloomacaua.game

import java.io.Serializable

class Player : Serializable {
    var id: String
    var name: String
    var cards: MutableList<Int>

    @Suppress("ConvertSecondaryConstructorToPrimary")
    @JvmOverloads
    constructor(
        id: String = "unknown",
        name: String = "waiting...",
        cards: MutableList<Int> = mutableListOf()
    ) {
        this.id = id
        this.name = name
        this.cards = cards
    }
}