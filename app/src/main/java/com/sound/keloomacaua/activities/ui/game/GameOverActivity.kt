package com.sound.keloomacaua.activities.ui.game

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sound.keloomacaua.R
import com.sound.keloomacaua.adaptors.MyCardDisplayAdapter
import com.sound.keloomacaua.game.CardUtils
import com.sound.keloomacaua.game.Game
import com.sound.keloomacaua.game.Player

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val game = intent.getSerializableExtra("game") as Game

        println("gameOver screen winner index is ${game.playersTurn}")

        val winner: Player = game.players[game.playersTurn]
        // FIXME: this assumes only 2 players
        val loser = game.players[game.players.size - game.playersTurn - 1]

        //winner
        val winnerText = findViewById<TextView>(R.id.txt_winner)
        winnerText.text = getString(R.string.txt_winner, winner.name);

        val winnerCard = findViewById<ImageView>(R.id.img_top_card);
        val imageTitleFromHand = CardUtils.getImageViewName(game.playedCards.last())
        val clickedImageId = resources.getIdentifier(
            imageTitleFromHand, "drawable",
            packageName
        )
        winnerCard.setImageResource(clickedImageId)

        val loserCardsAdapter = MyCardDisplayAdapter() { /* do nothing on card tap */ }
        val loserCards = findViewById<RecyclerView>(R.id.loser_cards)
        loserCards.adapter = loserCardsAdapter
        loserCardsAdapter.setOwnCards(loser.cards)

        val loserText = findViewById<TextView>(R.id.txt_loser)
        loserText.text = getString(R.string.txt_loser, loser.name)

        val btnDone: View = findViewById(R.id.btn_ok)
        btnDone.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO: this player can no longer look at the game. mark it as "deleted" and when all players mark it, then remove the game from DB
    }
}
