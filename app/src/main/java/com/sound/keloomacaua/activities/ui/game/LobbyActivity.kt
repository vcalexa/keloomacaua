package com.sound.keloomacaua.activities.ui.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sound.keloomacaua.Constants
import com.sound.keloomacaua.R
import com.sound.keloomacaua.adaptors.LobbyAdapter
import com.sound.keloomacaua.game.Game
import com.sound.keloomacaua.game.GameState
import com.sound.keloomacaua.game.Player

class LobbyActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    var lobbyAdapter: LobbyAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createorjoin)
        val dataRef = FirebaseDatabase.getInstance().reference
        val createGame = findViewById<Button>(R.id.createNewGame)
        val user = FirebaseAuth.getInstance().currentUser
        val userId: String
        val username: String?
        if (user == null) {
            // user is signed out, go back to sign-in form
            finish()
            return
        } else {
            userId = user.uid
            username = if (user.displayName != null) user.displayName else getString(R.string.waiting_for_player)
        }
        recyclerView = findViewById(R.id.gameList)
        lobbyAdapter = LobbyAdapter(userId, username)
        recyclerView?.adapter = lobbyAdapter
        dataRef.child(Constants.DB_COLLECTION_GAMES).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameList: MutableList<Game?> = ArrayList()
                for (gameSnapshot in snapshot.children) {
                    val game = gameSnapshot.getValue(Game::class.java)
                    if (game!!.state === GameState.Finished) {
                        gameSnapshot.ref.removeValue()
                    } else {
                        gameList.add(game)
                    }
                }
                lobbyAdapter!!.setCreatedGames(gameList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        createGame.setOnClickListener { view: View? ->
            val game = Game()
            val player = Player(userId, username!!)
            game.players.add(player)
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra(Constants.INTENT_EXTRA_GAME, game)
            startActivity(intent)
        }
    }
}