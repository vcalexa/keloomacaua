package com.sound.keloomacaua.adaptors

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.sound.keloomacaua.Constants
import com.sound.keloomacaua.R
import com.sound.keloomacaua.activities.ui.game.MainActivity
import com.sound.keloomacaua.game.Game
import com.sound.keloomacaua.game.GameState
import com.sound.keloomacaua.game.Player
import java.util.stream.Collectors

class LobbyAdapter(localUserId: String?, localUsername: String?) : RecyclerView.Adapter<LobbyAdapter.ViewHolder>() {
    private val createdGames: MutableList<Game>
    private val userId: String
    private val localUsername: String
    fun setCreatedGames(createdGames: MutableList<Game>) {
        val filteredGames = createdGames.stream().filter { game: Game ->
            (game.state === GameState.Started && game.findPlayer(userId) != -1
                    || game.state === GameState.Waiting)
        }.collect(Collectors.toList())
        this.createdGames.clear()
        this.createdGames.addAll(filteredGames)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_game, viewGroup, false)
        return ViewHolder(v)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, gameIndex: Int) {
        val game = createdGames[gameIndex]
        viewHolder.gameState.text = game.state.toString()
        if (game.players.size > 0) {
            viewHolder.player1Name.text = game.players[0].name
        }
        if (game.players.size > 1) {
            viewHolder.player2Name.text = game.players[1].name
        } else {
            viewHolder.player2Name.setText(R.string.waiting_for_player)
        }
        viewHolder.gameView.setOnClickListener { view: View ->
            if (game.state === GameState.Waiting && game.findPlayer(userId) == -1) {
                //create second player
                val player = Player(userId, localUsername)
                game.players.add(player)
            }
            //only join games that allow this user
            if (game.state !== GameState.Finished && game.findPlayer(userId) != -1) {
                val intent = Intent(view.context, MainActivity::class.java)
                intent.putExtra(Constants.INTENT_EXTRA_GAME, game)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                view.context.startActivity(intent)
            } else {
                Toast.makeText(view.context, R.string.game_already_started_by_others, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return createdGames.size
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var gameState: TextView = itemView.findViewById(R.id.gameState)
        var player1Name: TextView = itemView.findViewById(R.id.player1Name)
        var player2Name: TextView = itemView.findViewById(R.id.player2Name)
        var gameView: View = itemView.findViewById(R.id.lobbyItem)

    }

    init {
        createdGames = ArrayList()
        userId = localUserId ?: "unknown"
        this.localUsername = localUsername ?: "no name"
    }
}