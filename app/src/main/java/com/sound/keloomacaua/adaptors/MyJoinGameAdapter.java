package com.sound.keloomacaua.adaptors;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.activities.ui.game.MainActivity;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.game.Game;
import com.sound.keloomacaua.interfaces.ItemClickListener;

import java.util.List;

public class MyJoinGameAdapter extends RecyclerView.Adapter<MyJoinGameAdapter.ViewHolder> {
    private List<Game> createdGames;
    private final Context context;
    private DatabaseReference mdataRef;

    public MyJoinGameAdapter(Context context, List<Game> createdGames) {
        super();
        this.context = context;
        this.createdGames = createdGames;
    }

    public List<Game> getCreatedGames() {
        return createdGames;
    }

    public void setCreatedGames(List<Game> createdGames) {
        this.createdGames = createdGames;
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
        mdataRef = FirebaseDatabase.getInstance().getReference();

        viewHolder.player1Name.setOnClickListener(view -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mdataRef.child("games").child(String.valueOf(game.getGameId())).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Game game = snapshot.getValue(Game.class);
                    game.setPlayer1Joined(user.getEmail());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("game", game);
                    intent.putExtra("joinedFromListPlayer1", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        viewHolder.player2Name.setOnClickListener(view -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mdataRef.child("games").child(String.valueOf(game.getGameId())).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Game game = snapshot.getValue(Game.class);
                    game.setPlayer2Joined(user.getEmail());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("game", game);
                    intent.putExtra("joinedFromListPlayer2", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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