package com.sound.keloomacaua.activities.ui.game;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.adaptors.MyCardDisplayAdapter;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.Game;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView tableCard;
    public DatabaseReference mdataRef;
    public FirebaseUser user;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyCardDisplayAdapter bottomCardsAdaptor;

    public Button iaCarteButton;
    public Button gataTura;

    public boolean isPlayerOne;
    public boolean isPlayerTwo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        boolean joinFromListPlayer1 = getIntent().getBooleanExtra("joinedFromListPlayer1", false);
        boolean joinFromListPlayer2 = getIntent().getBooleanExtra("joinedFromListPlayer2", false);
        // To retrieve object in second Activity
        Game game = (Game) getIntent().getSerializableExtra("game");
        CardMoves cardMoves = CardMoves.getInstance();
        cardMoves.setGame(game);

        final List<Integer>[] playerCards = new List[]{new ArrayList<>()};
//        if (game.getPlayersTurn() == 1) {
//        } else if (game.getPlayersTurn() == 2) {
//        }

        mdataRef = FirebaseDatabase.getInstance().getReference();

        if (game.getPlayer1Joined().equals(user.getEmail())) {
            isPlayerOne = true;
            playerCards[0] = game.getPlayer1Cards();
        }

        if (game.getPlayer2Joined().equals(user.getEmail())) {
            isPlayerTwo = true;
            playerCards[0] = game.getPlayer2Cards();
        }


        mdataRef.child("games").child(String.valueOf(game.getGameId()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Game game = snapshot.getValue(Game.class);
                        game.setPlayersTurn(cardMoves.getPlayerTurn());
                        if (joinFromListPlayer1) {
                            game.setPlayer1Joined(user.getEmail());
                        }
                        if (joinFromListPlayer2) {
                            game.setPlayer2Joined(user.getEmail());
                        }

                        //mdataRef.child("games").child(String.valueOf(game.getGameId())).setValue(game);

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
        bottomCardsAdaptor = new MyCardDisplayAdapter(getApplicationContext(), playerCards[0], tableCard, this);
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
                            game.setPlayersTurn(cardMoves.getPlayerTurn());

                            if (isPlayerOne) {
                                game.setPlayer1Cards(cardMoves.getPlayer1Cards());
                                playerCards[0] = game.getPlayer1Cards();

                                //game.setPlayersTurn(2);
                                bottomCardsAdaptor.setActualCards(playerCards[0]);
                                iaCarteButton.setEnabled(false);
                                recyclerView.setAdapter(bottomCardsAdaptor);
                                recyclerView.scrollToPosition(finalPlayerCards.size() - 1);
                            }

                            if (isPlayerTwo) {
                                game.setPlayer2Cards(cardMoves.getPlayer2Cards());
                                playerCards[0] = game.getPlayer2Cards();

                                //game.setPlayersTurn(1);
                                bottomCardsAdaptor.setActualCards(playerCards[0]);
                                iaCarteButton.setEnabled(false);
                                recyclerView.setAdapter(bottomCardsAdaptor);
                                recyclerView.scrollToPosition(finalPlayerCards.size() - 1);
                            }

                            // mdataRef.child("games").child(String.valueOf(game.getGameId())).setValue(game);
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

    public boolean isPlayerOne() {
        return isPlayerOne;
    }
}