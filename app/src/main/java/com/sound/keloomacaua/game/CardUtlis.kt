package com.sound.keloomacaua.game

import android.content.Context
import androidx.annotation.DrawableRes
import java.io.Serializable

class CardUtils : Serializable {
    companion object {
        const val CARD_TWO = "two"
        const val CARD_THREE = "three"
        const val CARD_FOUR = "four"
        const val CARD_JOKER = "joker"
        const val CARD_ACE = "ace"
        const val SUITE_CLUBS = "clubs"
        const val SUITE_DIAMONDS = "diamonds"
        const val SUITE_HEARTS = "hearts"
        const val SUITE_SPADES = "spades"
        const val SUITE_BLACK = "black"
        const val SUITE_RED = "red"
        private val SUITS = arrayOf(SUITE_CLUBS, SUITE_DIAMONDS, SUITE_HEARTS, SUITE_SPADES)
        private val JOKER_SUITS = arrayOf(SUITE_BLACK, SUITE_RED)
        private val RANKS = arrayOf(
            CARD_ACE,
            CARD_TWO,
            CARD_THREE,
            CARD_FOUR,
            "five",
            "six",
            "seven",
            "eight",
            "nine",
            "ten",
            "jack",
            "queen",
            "king"
        )
        private val CARDS_PER_DECK = JOKER_SUITS.size + SUITS.size * RANKS.size
        val nameToCard = mutableMapOf<String, Int>()
        val cardToPair = mutableMapOf<Int, Pair<String, String>>()
        fun getImageViewName(n: Int): String {
            return if (n < 0) {
                "card_back"
            } else {
                val (first, second) = cardToPair[n] ?: ("unknown" to "unknown")
                String.format("%s_of_%s", first, second)
            }
        }

        fun getCardRank(cardNumber: Int): String {
            return cardToPair[cardNumber]?.first ?: "unknown"
        }

        fun getCardSuite(cardNumber: Int): String {
            return cardToPair[cardNumber]?.second ?: "unknown"
        }

        fun hasSameSuite(card1: Int, card2: Int): Boolean {
            return getCardSuite(card1) == getCardSuite(card2)
        }

        fun hasSameRank(card1: Int, card2: Int): Boolean {
            return getCardRank(card1) == getCardRank(card2)
        }

        fun cardHasRank(card: Int, rank: String): Boolean {
            return getCardRank(card) == rank
        }

        @DrawableRes
        fun cardToImageId(card: Int, context: Context): Int {
            val imageTitleFromCard = getImageViewName(card)
            return context.resources.getIdentifier(imageTitleFromCard, "drawable", context.packageName)
        }

        @DrawableRes
        fun suiteToImageId(suite: String?, context: Context): Int {
            return context.resources.getIdentifier(suite, "drawable", context.packageName)
        }

        fun <T> peekLast(list: MutableList<T>): T? {
            return if (list.isEmpty()) {
                null
            } else list[list.size - 1]
        }


        fun <T> removeLast(list: MutableList<T>): T? {
            return if (list.isEmpty()) {
                null
            } else list.removeLast()
        }


        init {
            var i = 0
            for (rank in RANKS) {
                for (suite in SUITS) {
                    cardToPair[i] = Pair(rank, suite)
                    nameToCard[rank + "_of_" + suite] = i
                    i++
                }
            }
            i = 0
            for (suite in JOKER_SUITS) {
                cardToPair[i] = Pair<String, String>(CARD_JOKER, suite)
                nameToCard[CARD_JOKER + "_of_" + suite] = i
                i++
            }
        }
    }
}