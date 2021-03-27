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

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val game = intent.getSerializableExtra(Constants.INTENT_EXTRA_GAME) as Game

        val winner: Player = game.players[game.playersTurn]
        // FIXME: this assumes only 2 players
        val loser = game.players[game.players.size - game.playersTurn - 1]

        //winner
        val winnerText = findViewById<TextView>(R.id.txt_winner)
        winnerText.text = getString(R.string.txt_winner, winner.name)

        val winnerCard = findViewById<ImageView>(R.id.img_top_card)
        winnerCard.setImageResource(CardUtils.cardToImageId(game.playedCards.last(), this))

        val loserCardsAdapter = MyCardDisplayAdapter() { /* do nothing on card tap */ }
        val loserCards = findViewById<RecyclerView>(R.id.loser_cards)
        loserCards.adapter = loserCardsAdapter
        loserCardsAdapter.setOwnCards(loser.cards)

        val loserText = findViewById<TextView>(R.id.txt_loser)
        loserText.text = getString(R.string.txt_loser, loser.name)

        val btnDone: View = findViewById(R.id.btn_ok)
        btnDone.setOnClickListener {
            val intent = Intent(this, LobbyActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}
