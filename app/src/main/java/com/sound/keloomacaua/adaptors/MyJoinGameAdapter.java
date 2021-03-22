package com.sound.keloomacaua.adaptors;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.activities.ui.game.MainActivity;
import com.sound.keloomacaua.game.Game;
import com.sound.keloomacaua.game.GameState;
import com.sound.keloomacaua.game.Player;

import java.util.ArrayList;
import java.util.List;

public class MyJoinGameAdapter extends RecyclerView.Adapter<MyJoinGameAdapter.ViewHolder> {
    private final List<Game> createdGames;

    public MyJoinGameAdapter() {
        super();
        this.createdGames = new ArrayList<>();
    }

    public void setCreatedGames(List<Game> createdGames) {
        this.createdGames.clear();
        this.createdGames.addAll(createdGames);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_game, viewGroup, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int gameIndex) {
        Game game = createdGames.get(gameIndex);
        viewHolder.gameState.setText(String.valueOf(game.getState().toString()));
        if (game.getPlayers().size() > 0) {
            viewHolder.player1Name.setText(game.getPlayers().get(0).getName());
        }
        if (game.getPlayers().size() > 1) {
            viewHolder.player2Name.setText(game.getPlayers().get(1).getName());
        } else {
            viewHolder.player2Name.setText(R.string.waiting_for_player);
        }

        viewHolder.gameView.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                // FIXME: should not be able to get here without user
                return;
            }
            String userId = user.getUid();
            String username = user.getDisplayName();
            username = username != null ? username : "no name";
            if (game.getState() == GameState.Waiting && game.hasPlayer(userId) == -1) {
                //create second player
                Player player = new Player(userId, username);
                game.getPlayers().add(player);
            }
            //only join games that allow this user
            if (game.hasPlayer(userId) != -1) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("game", game);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "You're not allowed to join this game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return createdGames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gameState;
        TextView player1Name;
        TextView player2Name;
        View gameView;

        ViewHolder(View itemView) {
            super(itemView);
            gameState = itemView.findViewById(R.id.gameState);
            player1Name = itemView.findViewById(R.id.player1Name);
            player2Name = itemView.findViewById(R.id.player2Name);
            gameView = itemView.findViewById(R.id.lobbyItem);
        }
    }
}