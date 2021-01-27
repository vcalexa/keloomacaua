package com.sound.keloomacaua.activities.ui.game;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.Game;

import java.time.LocalDate;
import java.util.Date;

public class CreateOrJoinActivity extends AppCompatActivity {
    private Button joinGame;
    private Button createGame;
    private DatabaseReference mdataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createorjoin);

        joinGame = findViewById(R.id.joinGame);
        createGame = findViewById(R.id.createNewGame);


        mdataRef = FirebaseDatabase.getInstance().getReference();

        createGame.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                CardMoves cardMoves = CardMoves.getInstance();

                Game game = new Game();
                game.setDateTime(new Date());
                game.setPlayer1Cards(cardMoves.getPlayer1Cards());
                game.setPlayer2Cards(cardMoves.getPlayer2Cards());
                game.setDeckRemainingCards(cardMoves.getDeckOfCards());
                game.setPlayer1Joined(user.getEmail());
                game.setPlayer2Joined("waiting...");
                game.setPlayedCards(cardMoves.getCardsPlayed());
                game.setWhoWon(-1);

                game.setPlayersTurn(cardMoves.getPlayerTurn());

                long gameId = System.currentTimeMillis();
                game.setGameId(gameId);
                mdataRef.child("games").child(String.valueOf(gameId)).setValue(game);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("game", game);
                startActivity(intent);
                finish();
            }
        });

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mdataRef.child("games").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }
}