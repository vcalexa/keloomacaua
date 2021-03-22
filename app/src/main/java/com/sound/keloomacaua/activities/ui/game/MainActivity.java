package com.sound.keloomacaua.activities.ui.game;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.sound.keloomacaua.game.GameState;

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
    int currentPlayerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // To retrieve object in second Activity
        Game game = (Game) getIntent().getSerializableExtra("game");
        currentPlayerIndex = game.findPlayer(user.getUid());

        CardMoves cardMoves = CardMoves.getInstance();
        cardMoves.setGame(game);
        cardMoves.setPlayer(currentPlayerIndex);
        if (game.getState() == GameState.Waiting && game.getPlayers().size() > 1) {
            game.setState(GameState.Started);
            cardMoves.deal();
        }

        mGameRef = FirebaseDatabase.getInstance().getReference().child("games").child(String.valueOf(game.getGameId()));
        mGameRef.setValue(game);

        tableCard = findViewById(R.id.tablePile);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycleViewCards);
        recyclerView.setLayoutManager(layoutManager);
        bottomCardsAdaptor = new MyCardDisplayAdapter(getApplicationContext(), mGameRef);
        recyclerView.setAdapter(bottomCardsAdaptor);

        recyclerView.scrollToPosition(cardMoves.localPlayerCards().size() - 1);

        iaCarteButton = findViewById(R.id.iaCarteId);
        leftOverCards = findViewById(R.id.leftOverTextView);

        iaCarteButton.setOnClickListener(view -> {
            cardMoves.pickCards(1);
            cardMoves.changeTurn();
            mGameRef.setValue(cardMoves.getGame());
        });

        gataTura = findViewById(R.id.gataTura);
        gataTura.setOnClickListener(view -> {
            cardMoves.changeTurn();
            mGameRef.setValue(cardMoves.getGame());
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

        //firebase becomes single source of truth
        cardMoves.setGame(game);

        // buttons
        boolean enableButtons = (currentPlayerIndex == game.getPlayersTurn() && cardMoves.getTopCard() != -1);
        iaCarteButton.setEnabled(enableButtons);
        gataTura.setEnabled(enableButtons);

        // top of pile
        String imageTitleFromHand = CardUtils.getImageViewName(cardMoves.getTopCard());
        int clickedImageId = getResources().getIdentifier(imageTitleFromHand, "drawable", getPackageName());
        tableCard.setImageResource(clickedImageId);

        // cards in hand
        List<Integer> cards = cardMoves.localPlayerCards();
        bottomCardsAdaptor.setOwnCards(cards);
        recyclerView.scrollToPosition(cards.size() - 1);

        int opponentCardsCount = cardMoves.getOpponentCardsCount();
        if (opponentCardsCount >= 0) {
            leftOverCards.setText(getString(R.string.opponent_cards_count, opponentCardsCount));
        } else {
            leftOverCards.setText(R.string.waiting_for_other_player);
        }
    }
}
