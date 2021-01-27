package com.sound.keloomacaua.activities.ui.game;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.adaptors.MyAdapter;
import com.sound.keloomacaua.game.Game;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView tableCard;
    private DatabaseReference mdataRef;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapter bottomCardsAdaptor;

    Button iaCarteButton;
    Button gataTura;

    boolean isPlayerOne;
    boolean isPlayerTwo;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To retrieve object in second Activity
        Game game = (Game) getIntent().getSerializableExtra("game");
        CardMoves cardMoves = CardMoves.getInstance();
        final List<Integer>[] playerCards = new List[]{new ArrayList<>()};
        if (game.getPlayersTurn() == 1) {
            playerCards[0] = game.getPlayer1Cards();
        } else if (game.getPlayersTurn() == 2) {
            playerCards[0] = game.getPlayer2Cards();
        }

        mdataRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (game.getPlayer1Joined().equals(user.getEmail())) {
            isPlayerOne = true;
        }

        if (game.getPlayer2Joined().equals(user.getEmail())) {
            isPlayerTwo = true;
        }

        mdataRef.child("games").child(String.valueOf(game.getGameId()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Game game = snapshot.getValue(Game.class);

                        if (isPlayerOne) {
                            if (game.getPlayersTurn() == 2) {
                                iaCarteButton.setEnabled(false);
                                gataTura.setEnabled(false);

                            } else {
                                iaCarteButton.setEnabled(true);
                                gataTura.setEnabled(true);
                            }
                        }

                        if (isPlayerTwo) {
                            if (game.getPlayersTurn() == 1) {
                                iaCarteButton.setEnabled(false);
                                gataTura.setEnabled(false);
                            } else {
                                iaCarteButton.setEnabled(true);
                                gataTura.setEnabled(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        tableCard = findViewById(R.id.tablePile);

        String firstCardTitle = cardMoves.getCardUtils().getImageViewName(cardMoves.getLast());
        int firstCardId = getResources().getIdentifier(firstCardTitle,
                "drawable", getPackageName());

        tableCard.setImageResource(firstCardId);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycleViewCards);

        recyclerView.setLayoutManager(layoutManager);
        bottomCardsAdaptor = new MyAdapter(getApplicationContext(), playerCards[0], tableCard);
        recyclerView.setAdapter(bottomCardsAdaptor);

        recyclerView.scrollToPosition(playerCards[0].size() - 1);

        iaCarteButton = findViewById(R.id.iaCarteId);
        List<Integer> finalPlayerCards = playerCards[0];
        iaCarteButton.setOnClickListener(view -> {

            cardMoves.player1Takes(1);
            cardMoves.changeTurn();

            mdataRef.child("games").child(String.valueOf(game.getGameId()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Game game = snapshot.getValue(Game.class);

                            game.setPlayedCards(cardMoves.getCardsPlayed());
                            game.setDeckRemainingCards(cardMoves.getDeckOfCards());

                            if (isPlayerOne) {
                                game.setPlayer1Cards(cardMoves.getPlayer1Cards());
                                playerCards[0] = game.getPlayer1Cards();

                                game.setPlayersTurn(2);
                                bottomCardsAdaptor.setActualCards(playerCards[0]);
                                iaCarteButton.setEnabled(false);
                                recyclerView.setAdapter(bottomCardsAdaptor);
                                recyclerView.scrollToPosition(finalPlayerCards.size() - 1);
                            }

                            if (isPlayerTwo) {
                                game.setPlayer2Cards(cardMoves.getPlayer1Cards());
                                playerCards[0] = game.getPlayer2Cards();

                                game.setPlayersTurn(1);
                                bottomCardsAdaptor.setActualCards(playerCards[0]);
                                iaCarteButton.setEnabled(false);
                                recyclerView.setAdapter(bottomCardsAdaptor);
                                recyclerView.scrollToPosition(finalPlayerCards.size() - 1);
                            }

                            mdataRef.child("games").child(String.valueOf(game.getGameId()))
                                    .setValue(game);
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



        });

        gataTura = findViewById(R.id.gataTura);
        gataTura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iaCarteButton.setEnabled(false);
                gataTura.setEnabled(false);
                cardMoves.changeTurn();
            }
        });
    }
}