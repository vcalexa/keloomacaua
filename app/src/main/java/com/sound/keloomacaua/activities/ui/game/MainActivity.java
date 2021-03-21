package com.sound.keloomacaua.activities.ui.game;

import static java.lang.String.format;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.game.Game;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView tableCard;
    public DatabaseReference mGameRef;
    public FirebaseUser user;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyCardDisplayAdapter bottomCardsAdaptor;

    public Button iaCarteButton;
    public Button gataTura;
    public TextView leftOverCards;

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

        mGameRef = FirebaseDatabase.getInstance().getReference()
                .child("games").child(String.valueOf(game.getGameId()));

        if (joinFromListPlayer1) {
            game.setPlayer1Joined(user.getDisplayName());
        }
        if (joinFromListPlayer2) {
            game.setPlayer2Joined(user.getDisplayName());
        }

        if (game.getPlayer1Joined().equals(user.getDisplayName())) {
            isPlayerOne = true;
        }

        if (game.getPlayer2Joined().equals(user.getDisplayName())) {
            isPlayerTwo = true;
        }

        cardMoves.setPlayer(isPlayerOne ? 1 : 2);
        cardMoves.setGame(game);

        tableCard = findViewById(R.id.tablePile);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycleViewCards);
        recyclerView.setLayoutManager(layoutManager);
        bottomCardsAdaptor = new MyCardDisplayAdapter(getApplicationContext(), isPlayerOne, mGameRef);
        recyclerView.setAdapter(bottomCardsAdaptor);

        recyclerView.scrollToPosition(cardMoves.localPlayerCards().size() - 1);

        iaCarteButton = findViewById(R.id.iaCarteId);
        leftOverCards = findViewById(R.id.leftOverTextView);
        leftOverCards.setText(format("Mai are %s carti.", cardMoves.getOpponentCardsCount()));
        iaCarteButton.setOnClickListener(view -> {
            if (isPlayerOne) {
                cardMoves.player1Takes(1);
            } else {
                cardMoves.player2Takes(1);
            }
            cardMoves.changeTurn();
            mGameRef.setValue(cardMoves.getGame());
        });

        gataTura = findViewById(R.id.gataTura);
        gataTura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardMoves.changeTurn();
                mGameRef.setValue(cardMoves.getGame());
            }
        });

        mGameRef.setValue(game);

        mGameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Game game = snapshot.getValue(Game.class);
                updateStateFromGame(game);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateStateFromGame(Game game) {
        CardMoves cardMoves = CardMoves.getInstance();
        CardUtils cardUtils = cardMoves.getCardUtils();

        //firebase becomes single source of truth
        cardMoves.setGame(game);

        // buttons
        int turn = game.getPlayersTurn();
        boolean enableButtons = (isPlayerOne && turn == 1) || (!isPlayerOne && turn == 2);
        iaCarteButton.setEnabled(enableButtons);
        gataTura.setEnabled(enableButtons);

        // top of pile
        String imageTitleFromHand = cardUtils.getImageViewName(cardMoves.getLast());
        int clickedImageId = getResources().getIdentifier(imageTitleFromHand, "drawable", getPackageName());
        tableCard.setImageResource(clickedImageId);

        // cards in hand
        List<Integer> cards = cardMoves.localPlayerCards();
        bottomCardsAdaptor.setOwnCards(cards);
        recyclerView.scrollToPosition(cards.size() - 1);
    }
}
