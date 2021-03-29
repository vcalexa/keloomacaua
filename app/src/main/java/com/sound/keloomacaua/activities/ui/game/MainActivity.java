package com.sound.keloomacaua.activities.ui.game;

import static com.sound.keloomacaua.Constants.DB_COLLECTION_GAMES;
import static com.sound.keloomacaua.Constants.INTENT_EXTRA_GAME;
import static com.sound.keloomacaua.game.CardUtils.SUITE_CLUBS;
import static com.sound.keloomacaua.game.CardUtils.SUITE_DIAMONDS;
import static com.sound.keloomacaua.game.CardUtils.SUITE_HEARTS;
import static com.sound.keloomacaua.game.CardUtils.SUITE_SPADES;
import static com.sound.keloomacaua.game.CardUtils.cardToImageId;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sound.keloomacaua.adaptors.OpponentCardsAdapter;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.game.Game;
import com.sound.keloomacaua.game.GameState;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public DatabaseReference mGameRef;
    public FirebaseUser user;

    MyCardDisplayAdapter bottomCardsAdaptor;
    OpponentCardsAdapter opponentCardsAdapter;

    RecyclerView cardsInHand;
    RecyclerView opponentCards;
    ImageView imgTopCard;
    TextView btnTakeCards;
    Button btnDone;
    TextView txtOpponentCardsCount;
    View btnClubs;
    View btnDiamonds;
    View btnHearts;
    View btnSpades;
    ImageView imgSuiteOverride;
    int localPlayerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnClubs = findViewById(R.id.switch_to_clubs);
        btnDiamonds = findViewById(R.id.switch_to_diamonds);
        btnHearts = findViewById(R.id.switch_to_hearts);
        btnSpades = findViewById(R.id.switch_to_spades);
        imgSuiteOverride = findViewById(R.id.suite_override);
        btnDone = findViewById(R.id.btn_turn);
        imgTopCard = findViewById(R.id.img_top_card);
        cardsInHand = findViewById(R.id.cards_in_hand);
        opponentCards = findViewById(R.id.img_opponent_cards);
        btnTakeCards = findViewById(R.id.btn_take_cards);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // To retrieve object in second Activity
        Game game = (Game) getIntent().getSerializableExtra(INTENT_EXTRA_GAME);
        localPlayerIndex = game.findPlayer(user.getUid());

        CardMoves cardMoves = CardMoves.getInstance();
        cardMoves.setGame(game);
        cardMoves.setPlayer(localPlayerIndex);
        if (game.getState() == GameState.Waiting && game.getPlayers().size() > 1) {
            game.setState(GameState.Started);
            cardMoves.deal();
        }

        mGameRef = FirebaseDatabase.getInstance().getReference().child(DB_COLLECTION_GAMES).child(String.valueOf(game.getGameId()));
        mGameRef.setValue(game);

        bottomCardsAdaptor = new MyCardDisplayAdapter((cardPosition) -> {
            String cardTitle = CardUtils.getImageViewName(cardMoves.localPlayerCards().get(cardPosition));
            if (cardMoves.canPlayCardAt(cardPosition)) {
                cardMoves.playCardAt(cardPosition);
                mGameRef.setValue(cardMoves.getGame());
            } else {
                Toast.makeText(this, getString(R.string.cannot_play_card, cardTitle), Toast.LENGTH_SHORT).show();
            }
        });
        cardsInHand.setAdapter(bottomCardsAdaptor);
        cardsInHand.scrollToPosition(cardMoves.localPlayerCards().size() - 1);

        opponentCardsAdapter = new OpponentCardsAdapter();
        opponentCards.setAdapter(opponentCardsAdapter);

        txtOpponentCardsCount = findViewById(R.id.txt_opponent_cards);

        btnTakeCards.setOnClickListener(view -> {
            cardMoves.drawCards();
            cardsInHand.scrollToPosition(cardMoves.localPlayerCards().size() - 1);
            mGameRef.setValue(cardMoves.getGame());
        });

        btnDone.setOnClickListener(view -> {
            cardMoves.endTurn();
            mGameRef.setValue(cardMoves.getGame());
        });

        mGameRef.setValue(game);

        mGameRef.addValueEventListener(gameUpdateListener);

        View.OnClickListener suiteListener = view -> {
            String suite;
            int id = view.getId();
            if (id == R.id.switch_to_clubs) {
                suite = SUITE_CLUBS;
            } else if (id == R.id.switch_to_diamonds) {
                suite = SUITE_DIAMONDS;
            } else if (id == R.id.switch_to_hearts) {
                suite = SUITE_HEARTS;
            } else if (id == R.id.switch_to_spades) {
                suite = SUITE_SPADES;
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

    @Override
    protected void onDestroy() {
        mGameRef.removeEventListener(gameUpdateListener);
        super.onDestroy();
    }

    private final ValueEventListener gameUpdateListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Game game = snapshot.getValue(Game.class);
            if (game != null) {
                updateStateFromGame(game);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    private void updateStateFromGame(Game game) {
        CardMoves cardMoves = CardMoves.getInstance();

        //firebase becomes single source of truth
        cardMoves.setGame(game);

        if (game.getState() == GameState.Finished) {
            startGameOverScreen();
            return;
        }

        // buttons
        int pickSuiteVisibility = cardMoves.canPickSuite() ? View.VISIBLE : View.INVISIBLE;
        btnClubs.setVisibility(pickSuiteVisibility);
        btnDiamonds.setVisibility(pickSuiteVisibility);
        btnHearts.setVisibility(pickSuiteVisibility);
        btnSpades.setVisibility(pickSuiteVisibility);

        btnTakeCards.setEnabled(cardMoves.canTakeCards());
        btnDone.setEnabled(cardMoves.canEndTurn());

        if (game.owedCards > 0 && game.getPlayersTurn() == localPlayerIndex) {
            btnTakeCards.setText(getString(R.string.draw_n_cards, game.owedCards));
        } else {
            btnTakeCards.setText(getString(R.string.draw_card));
        }

        if (game.getActiveSkipTurns() > 0 && game.getPlayerToSkipTurn() == localPlayerIndex) {
            btnDone.setText(
                    game.getActiveSkipTurns() > 1 ?
                            getString(R.string.skip_n_turns, game.getActiveSkipTurns()) :
                            getString(R.string.skip_1_turn)
            );
        } else {
            btnDone.setText(R.string.end_turn);
        }

        if (game.suiteOverride.isEmpty()) {
            imgSuiteOverride.setVisibility(View.GONE);
        } else {
            imgSuiteOverride.setVisibility(View.VISIBLE);
            imgSuiteOverride.setImageResource(CardUtils.suiteToImageId(game.suiteOverride, this));
        }

        // top of pile
        imgTopCard.setImageResource(cardToImageId(cardMoves.getTopCard(), this));

        // cards in hand
        List<Integer> cards = cardMoves.localPlayerCards();
        bottomCardsAdaptor.setOwnCards(cards);

        int opponentCardsCount = cardMoves.getOpponentCardsCount();

        if (opponentCardsCount >= 0) {
            txtOpponentCardsCount.setText(getString(R.string.opponent_cards_count, opponentCardsCount));
            opponentCardsAdapter.setCards(opponentCardsCount);
        } else {
            txtOpponentCardsCount.setText(R.string.waiting_for_other_player);
        }
    }

    private void startGameOverScreen() {
        mGameRef.removeValue();
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra(INTENT_EXTRA_GAME, CardMoves.getInstance().getGame());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
