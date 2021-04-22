package com.sound.keloomacaua.activities.ui.game

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.sound.keloomacaua.Constants
import com.sound.keloomacaua.R
import com.sound.keloomacaua.game.CardMoves
import com.sound.keloomacaua.game.CardUtils.nameToCard
import com.sound.keloomacaua.game.Game
import com.sound.keloomacaua.game.GameState
import com.sound.keloomacaua.game.Player
import org.junit.Test

class GameOverActivityTest {
//    @get:Rule
//    var activityRule: ActivityScenarioRule<GameOverActivity> =
//        ActivityScenarioRule(GameOverActivity::class.java)

    val testContext: Context
        get() = ApplicationProvider.getApplicationContext()

    fun createDummyGame(playerTurn: Int = 0): Game {
        val game = Game()
        game.playersTurn = playerTurn
        game.state = GameState.Started
        game.gameId = 1234L
        game.playedCards = mutableListOf(
            nameToCard.get("ace_of_spades") ?: -1
        )
        val winner = Player("bula_id", "bula")
        val loser = Player(
            "strula_id", "strula",
            listOf(
                nameToCard["ace_of_clubs"],
                nameToCard["two_of_hearts"],
                nameToCard["joker_of_red"],
            ).filterNotNull().toMutableList()
        )
        game.players = mutableListOf(winner, loser)
        return game
    }

    @Test
    fun gameOverWinner() {
        val game = createDummyGame(0)
        CardMoves.getInstance().game = game
        val gameOverIntent = Intent(testContext, GameOverActivity::class.java)
        gameOverIntent.putExtra(Constants.INTENT_EXTRA_GAME, game)
        gameOverIntent.putExtra(Constants.INTENT_EXTRA_CURRENT_PLAYER, 0)
        val scenario = ActivityScenario.launch<GameOverActivity>(gameOverIntent)
        onView(withId(R.id.txt_game_over)).check(matches(isDisplayed()))
        onView(withId(R.id.loser_cards)).check(matches(hasChildCount(3)))
    }

    @Test
    fun gameOverLoser() {
        val game = createDummyGame(0)
        CardMoves.getInstance().game = game
        val gameOverIntent = Intent(testContext, GameOverActivity::class.java)
        gameOverIntent.putExtra(Constants.INTENT_EXTRA_GAME, game)
        gameOverIntent.putExtra(Constants.INTENT_EXTRA_CURRENT_PLAYER, 1)
        val scenario = ActivityScenario.launch<GameOverActivity>(gameOverIntent)
        onView(withId(R.id.txt_game_over)).check(matches(isDisplayed()))
        onView(withId(R.id.loser_cards)).check(matches(hasChildCount(3)))
    }

}