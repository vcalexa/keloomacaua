package com.sound.keloomacaua.activities.ui.game

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
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

        val mGameRef = FirebaseDatabase
            .getInstance()
            .reference
            .child("games")
            .child(game.gameId.toString())

        val winner: Player = game.players[game.whoWon]
        val loser = game.players[game.players.size - game.whoWon - 1]

        val bottomCardsAdaptor = MyCardDisplayAdapter(applicationContext, mGameRef)
        val loserCards = findViewById<RecyclerView>(R.id.opponent_cards)
        loserCards.adapter = bottomCardsAdaptor
        bottomCardsAdaptor.setOwnCards(loser.cards)

        val winnerText = findViewById<TextView>(R.id.txt_winner)
        winnerText.text = getString(R.string.txt_winner, winner.name);

        val winnerCard = findViewById<ImageView>(R.id.img_top_card);
        val imageTitleFromHand = CardUtils.getImageViewName(game.playedCards.last())
        val clickedImageId = resources.getIdentifier(
            imageTitleFromHand, "drawable",
            packageName
        )
        winnerCard.setImageResource(clickedImageId)

        val loserText = findViewById<TextView>(R.id.txt_loser)
        loserText.text = getString(R.string.txt_loser, loser.name)

        val btnDone: View = findViewById(R.id.btn_ok)
        btnDone.setOnClickListener {
            finish()
        }
    }
}
