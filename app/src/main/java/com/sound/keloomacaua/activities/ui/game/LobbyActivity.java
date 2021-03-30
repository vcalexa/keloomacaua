package com.sound.keloomacaua.activities.ui.game;

import static com.sound.keloomacaua.Constants.DB_COLLECTION_GAMES;
import static com.sound.keloomacaua.Constants.INTENT_EXTRA_GAME;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.adaptors.MyJoinGameAdapter;
import com.sound.keloomacaua.game.Game;
import com.sound.keloomacaua.game.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MyJoinGameAdapter myJoinGameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createorjoin);
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

        Button createGame = findViewById(R.id.createNewGame);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId;
        String username;
        if (user == null) {
            // user is signed out, go back to sign-in form
            finish();
            return;
        } else {
            userId = user.getUid();
            username = user.getDisplayName() != null ? user.getDisplayName() : getString(R.string.waiting_for_player);
        }

        recyclerView = findViewById(R.id.gameList);

        myJoinGameAdapter = new MyJoinGameAdapter(userId, username);
        recyclerView.setAdapter(myJoinGameAdapter);

        dataRef.child(DB_COLLECTION_GAMES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Game> gameList = new ArrayList<>();
                for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                    Game game = gameSnapshot.getValue(Game.class);
                    gameList.add(game);
                }
                myJoinGameAdapter.setCreatedGames(gameList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        createGame.setOnClickListener(view -> {
            Game game = new Game();
            Player player = new Player(userId, username);
            game.getPlayers().add(player);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(INTENT_EXTRA_GAME, game);
            startActivity(intent);
        });

    }
}