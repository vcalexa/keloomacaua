package com.sound.keloomacaua.activities.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sound.keloomacaua.Constants
import com.sound.keloomacaua.R
import com.sound.keloomacaua.adaptors.MyCardDisplayAdapter
import com.sound.keloomacaua.game.CardUtils
import com.sound.keloomacaua.game.Game
import com.sound.keloomacaua.game.Player
import com.sound.keloomacaua.interfaces.CardTapListener
import kotlin.random.Random

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val game = intent.getSerializableExtra(Constants.INTENT_EXTRA_GAME) as Game
        val localPlayer =
            intent.getSerializableExtra(Constants.INTENT_EXTRA_CURRENT_PLAYER) as Int? ?: -1

        val localPlayerWon = localPlayer == game.playersTurn

        val gameOverText = findViewById<TextView>(R.id.txt_game_over)
        gameOverText.text = getGameOverText(localPlayerWon)

        val winner: Player = game.players[game.playersTurn]
        val winnerName =
            if (localPlayerWon) getString(R.string.local_player_pronoun) else winner.name
        // FIXME: this assumes only 2 players
        val loser: Player = game.players[(game.playersTurn + 1) % game.players.size]
        val loserName = if (localPlayerWon) loser.name else getString(R.string.local_player_pronoun)

        //winner
        val winnerText = findViewById<TextView>(R.id.txt_winner)
        winnerText.text = getString(R.string.txt_winner, winnerName)

        val winnerCard = findViewById<ImageView>(R.id.img_winner_card)
        winnerCard.setImageResource(CardUtils.cardToImageId(game.playedCards.last(), this))

        val loserCardsAdapter = MyCardDisplayAdapter() 
        val loserCards = findViewById<RecyclerView>(R.id.loser_cards)
        loserCards.adapter = loserCardsAdapter
        loserCardsAdapter.setOwnCards(loser.cards)

        val loserText = findViewById<TextView>(R.id.txt_loser)
        loserText.text = getString(R.string.txt_loser, loserName)

        val btnDone: View = findViewById(R.id.btn_ok)
        btnDone.setOnClickListener {
            val intent = Intent(this, LobbyActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun getGameOverText(localPlayerWon: Boolean) = if (localPlayerWon) {
        val happyFace = Random(System.currentTimeMillis()).nextInt(winnerFaces.size)
        getString(R.string.game_over_winner, winnerFaces[happyFace])
    } else {
        val sadFace = Random(System.currentTimeMillis()).nextInt(loserFaces.size)
        getString(R.string.game_over_loser, loserFaces[sadFace])
    }
}

val winnerFaces = arrayOf(
    ":-)",
    ":-D",
    "^_^",
    "٩(^‿^)۶",
        "¯\\_(ツ)_/¯\n" +
                "\n",
"\uD83D\uDE0A",
    "\uD83D\uDE04",
    "\uD83D\uDE01",
    "\uD83D\uDE06",
    "\uD83D\uDE42",
    "\uD83D\uDE09",
    "\uD83D\uDE0A",
    "\uD83D\uDE0E",
    "\uD83D\uDE0F"
)
val loserFaces = arrayOf(
    ":-(",
    "•`_´•",
    "\uD83E\uDD14",
    "\uD83E\uDD28",
    "\uD83D\uDE10",
    "\uD83D\uDE12",
    "\uD83D\uDE44",
    "\uD83D\uDE35",
    "☹",
    "\uD83D\uDE26",
    "\uD83D\uDE2D"
)
