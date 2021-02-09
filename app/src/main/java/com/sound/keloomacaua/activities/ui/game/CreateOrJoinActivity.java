package com.sound.keloomacaua.activities.ui.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.adaptors.MyCardDisplayAdapter;
import com.sound.keloomacaua.adaptors.MyJoinGameAdapter;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.Game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateOrJoinActivity extends AppCompatActivity {
    //private Button joinFirstAvailableGame;
    private Button createGame;
    private DatabaseReference mdataRef;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyJoinGameAdapter myJoinGameAdapter;
    List<Game> gameList;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createorjoin);
        mdataRef = FirebaseDatabase.getInstance().getReference();

        //joinFirstAvailableGame = findViewById(R.id.joinGame);
        createGame = findViewById(R.id.createNewGame);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // user is signed in, show user data
        } else {
            // user is signed out, show sign-in form
        }

        mdataRef.child("games").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gameList = new ArrayList<>();
                for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                    Game game = gameSnapshot.getValue(Game.class);
                    gameList.add(game);
                }
                recyclerView.setLayoutManager(layoutManager);
                myJoinGameAdapter = new MyJoinGameAdapter(getApplicationContext(), gameList);
                recyclerView.setAdapter(myJoinGameAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = findViewById(R.id.gameList);

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CardMoves cardMoves = CardMoves.getInstance();
                cardMoves.createNewGame();

                Game game = cardMoves.getGame();
                game.setDateTime(new Date());
                game.setPlayer1Joined(user.getEmail());
                game.setPlayer2Joined("waiting...");
                game.setWhoWon(-1);

                long gameId = System.currentTimeMillis();
                game.setGameId(gameId);

                mdataRef.child("games").child(String.valueOf(gameId)).setValue(game);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("game", game);
                startActivity(intent);
            }
        });

        /*joinFirstAvailableGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mdataRef.child("games").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                            Game game = gameSnapshot.getValue(Game.class);

                            if (game.getPlayer1Joined() != null) {
                                game.setPlayer2Joined(user.getEmail());

                                CardMoves cardMoves = CardMoves.getEmptyInstance();
                                cardMoves.setCardsPlayed(game.getPlayedCards());
                                cardMoves.setPlayer1Cards(game.getPlayer1Cards());
                                cardMoves.setPlayer2Cards(game.getPlayer2Cards());
                                cardMoves.setPlayerTurn(2);
                                cardMoves.setDeckOfCards(game.getDeckRemainingCards());

                                mdataRef.child("games").child(String.valueOf(game.getGameId()))
                                        .setValue(game);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("game", game);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });*/


    }
}