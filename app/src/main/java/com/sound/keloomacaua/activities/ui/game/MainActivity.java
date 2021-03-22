package com.sound.keloomacaua.activities.ui.game;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sound.keloomacaua.adaptors.MyCardDisplayAdapter;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.game.Game;
import com.sound.keloomacaua.game.GameState;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public DatabaseReference mGameRef;
    public FirebaseUser user;

    MyCardDisplayAdapter bottomCardsAdaptor;

    RecyclerView cardsInHand;
    ImageView imgTopCard;
    TextView btnPickCards;
    View btnDone;
    TextView txtOpponentCardsCount;
    View btnUndo;
    View btnClubs;
    View btnDiamonds;
    View btnHearts;
    View btnSpades;
    ImageView imgSuiteOverride;
    int currentPlayerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUndo = findViewById(R.id.btn_undo);
        btnClubs = findViewById(R.id.switch_to_clubs);
        btnDiamonds = findViewById(R.id.switch_to_diamonds);
        btnHearts = findViewById(R.id.switch_to_hearts);
        btnSpades = findViewById(R.id.switch_to_spades);
        imgSuiteOverride = findViewById(R.id.suite_override);
        btnDone = findViewById(R.id.btn_done);
        imgTopCard = findViewById(R.id.img_top_card);
        cardsInHand = findViewById(R.id.cards_in_hand);
        btnPickCards = findViewById(R.id.btn_take_cards);

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

        bottomCardsAdaptor = new MyCardDisplayAdapter(getApplicationContext(), mGameRef);
        cardsInHand.setAdapter(bottomCardsAdaptor);

        cardsInHand.scrollToPosition(cardMoves.localPlayerCards().size() - 1);

        txtOpponentCardsCount = findViewById(R.id.txt_opponent_cards);

        btnPickCards.setOnClickListener(view -> {
            cardMoves.pickCards(1);
            mGameRef.setValue(cardMoves.getGame());
        });

        btnDone.setOnClickListener(view -> {
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

        View.OnClickListener suiteListener = view -> {
            String suite = "";
            int id = view.getId();
            if (id == R.id.switch_to_clubs) {
                suite = "clubs";
            } else if (id == R.id.switch_to_diamonds) {
                suite = "diamonds";
            } else if (id == R.id.switch_to_hearts) {
                suite = "hearts";
            } else if (id == R.id.switch_to_spades) {
                suite = "spades";
            } else {
                return;
            }
            cardMoves.changeSuite(suite);
            mGameRef.setValue(cardMoves.getGame());
        };
        btnClubs.setOnClickListener(suiteListener);
        btnDiamonds.setOnClickListener(suiteListener);
        btnHearts.setOnClickListener(suiteListener);
        btnSpades.setOnClickListener(suiteListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateStateFromGame(Game game) {
        CardMoves cardMoves = CardMoves.getInstance();

        //firebase becomes single source of truth
        cardMoves.setGame(game);

        // buttons
        boolean enableButtons = (currentPlayerIndex == game.getPlayersTurn() && cardMoves.getTopCard() != -1);
        btnPickCards.setEnabled(enableButtons);
        btnDone.setEnabled(enableButtons);

        boolean pickSuiteActive = game.playerPicksSuite == cardMoves.getPlayer();
        int pickSuiteVisibility = pickSuiteActive ? View.VISIBLE : View.INVISIBLE;
        btnClubs.setVisibility(pickSuiteVisibility);
        btnDiamonds.setVisibility(pickSuiteVisibility);
        btnHearts.setVisibility(pickSuiteVisibility);
        btnSpades.setVisibility(pickSuiteVisibility);
        if (pickSuiteActive) {
            btnDone.setEnabled(false);
        }

        if (game.suiteOverride.isEmpty()) {
            imgSuiteOverride.setVisibility(View.INVISIBLE);
        } else {
            imgSuiteOverride.setVisibility(View.VISIBLE);
            int suiteImageId = getResources().getIdentifier(game.suiteOverride, "drawable", getPackageName());
            imgSuiteOverride.setImageResource(suiteImageId);
        }

        // top of pile
        String imageTitleFromHand = CardUtils.getImageViewName(cardMoves.getTopCard());
        int clickedImageId = getResources().getIdentifier(imageTitleFromHand, "drawable", getPackageName());
        imgTopCard.setImageResource(clickedImageId);

        // cards in hand
        List<Integer> cards = cardMoves.localPlayerCards();
        bottomCardsAdaptor.setOwnCards(cards);
        cardsInHand.scrollToPosition(cards.size() - 1);

        int opponentCardsCount = cardMoves.getOpponentCardsCount();
        if (opponentCardsCount >= 0) {
            txtOpponentCardsCount.setText(getString(R.string.opponent_cards_count, opponentCardsCount));
        } else {
            txtOpponentCardsCount.setText(R.string.waiting_for_other_player);
        }
    }
}
