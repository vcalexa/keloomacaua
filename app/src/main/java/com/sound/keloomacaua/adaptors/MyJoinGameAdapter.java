package com.sound.keloomacaua.adaptors;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.activities.ui.game.MainActivity;
import com.sound.keloomacaua.game.Game;

import java.util.ArrayList;
import java.util.List;

public class MyJoinGameAdapter extends RecyclerView.Adapter<MyJoinGameAdapter.ViewHolder> {
    private final List<Game> createdGames;
    private final DatabaseReference mdataRef;

    public MyJoinGameAdapter(DatabaseReference gameDatabase) {
        super();
        this.mdataRef = gameDatabase;
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

        viewHolder.gameId.setText(String.valueOf(game.getGameId()));
        viewHolder.player1Name.setText(game.getPlayer1Joined());
        viewHolder.player2Name.setText(game.getPlayer2Joined());

        viewHolder.player1Name.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            game.setPlayer1Joined(user.getDisplayName());
            mdataRef.child("games").child(String.valueOf(game.getGameId())).setValue(game);
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra("game", game);
            intent.putExtra("joinedFromListPlayer1", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        });

        viewHolder.player2Name.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            game.setPlayer2Joined(user.getDisplayName());
            mdataRef.child("games").child(String.valueOf(game.getGameId())).setValue(game);
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            intent.putExtra("game", game);
            intent.putExtra("joinedFromListPlayer2", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return createdGames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gameId;
        Button player1Name;
        Button player2Name;

        ViewHolder(View itemView) {
            super(itemView);
            gameId = itemView.findViewById(R.id.gameId);
            player1Name = itemView.findViewById(R.id.player1Name);
            player2Name = itemView.findViewById(R.id.player2Name);
        }
    }
}