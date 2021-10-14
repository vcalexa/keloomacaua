package com.sound.keloomacaua.activities.ui.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.sound.keloomacaua.Constants
import com.sound.keloomacaua.R
import com.sound.keloomacaua.activities.ui.login.LoginActivity
import com.sound.keloomacaua.adaptors.MyCardDisplayAdapter
import com.sound.keloomacaua.adaptors.OpponentCardsAdapter
import com.sound.keloomacaua.game.CardMoves
import com.sound.keloomacaua.game.CardUtils
import com.sound.keloomacaua.game.CardUtils.Companion.cardToImageId
import com.sound.keloomacaua.game.Game
import com.sound.keloomacaua.game.GameState
import com.sound.keloomacaua.interfaces.CardTapListener

class MainActivity : AppCompatActivity() {
    lateinit var mGameRef: DatabaseReference
    private lateinit var user: FirebaseUser
    private lateinit var bottomCardsAdaptor: MyCardDisplayAdapter
    private lateinit var opponentCardsAdapter: OpponentCardsAdapter
    private lateinit var cardsInHand: RecyclerView
    private lateinit var opponentCards: RecyclerView
    private lateinit var imgTopCard: ImageView
    private lateinit var btnTakeCards: TextView
    private lateinit var btnDone: Button
    private lateinit var txtOpponentCardsCount: TextView
    private lateinit var btnClubs: View
    private lateinit var btnDiamonds: View
    private lateinit var btnHearts: View
    private lateinit var btnSpades: View
    private lateinit var imgSuiteOverride: ImageView
    private var localPlayerIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnClubs = findViewById(R.id.switch_to_clubs)
        btnDiamonds = findViewById(R.id.switch_to_diamonds)
        btnHearts = findViewById(R.id.switch_to_hearts)
        btnSpades = findViewById(R.id.switch_to_spades)
        imgSuiteOverride = findViewById(R.id.suite_override)
        btnDone = findViewById(R.id.btn_turn)
        imgTopCard = findViewById(R.id.img_top_card)
        cardsInHand = findViewById(R.id.cards_in_hand)
        opponentCards = findViewById(R.id.img_opponent_cards)
        btnTakeCards = findViewById(R.id.btn_take_cards)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            user = currentUser
        }

        // To retrieve object in second Activity
        val game = intent.getSerializableExtra(Constants.INTENT_EXTRA_GAME) as Game
        localPlayerIndex = game.findPlayer(user.uid)
        CardMoves.game = game
        CardMoves.player = localPlayerIndex
        if (game.state === GameState.Waiting && game.players.size > 1) {
            game.state = GameState.Started
            CardMoves.deal()
        }
        mGameRef = FirebaseDatabase.getInstance().reference.child(Constants.DB_COLLECTION_GAMES)
            .child(game.gameId.toString())
        mGameRef.setValue(game)
        bottomCardsAdaptor = MyCardDisplayAdapter(object : CardTapListener {
            override fun onCardTapped(cardPosition: Int) {
                val cardTitle =
                    CardUtils.getImageViewName(CardMoves.localPlayerCards()[cardPosition])
                if (CardMoves.canPlayCardAt(cardPosition)) {
                    CardMoves.playCardAt(cardPosition)
                    mGameRef.setValue(CardMoves.game)
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.cannot_play_card, cardTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        cardsInHand.adapter = bottomCardsAdaptor
        cardsInHand.scrollToPosition(CardMoves.localPlayerCards().size - 1)
        opponentCardsAdapter = OpponentCardsAdapter()
        opponentCards.adapter = opponentCardsAdapter
        txtOpponentCardsCount = findViewById(R.id.txt_opponent_cards)
        btnTakeCards.setOnClickListener {
            CardMoves.drawCards()
            cardsInHand.scrollToPosition(CardMoves.localPlayerCards().size - 1)
            mGameRef.setValue(CardMoves.game)
        }
        btnDone.setOnClickListener {
            CardMoves.endTurn()
            mGameRef.setValue(CardMoves.game)
        }
        mGameRef.setValue(game)
        mGameRef.addValueEventListener(gameUpdateListener)
        val suiteListener = View.OnClickListener { view: View ->
            val suite = when (view.id) {
                R.id.switch_to_clubs -> CardUtils.SUITE_CLUBS
                R.id.switch_to_diamonds -> CardUtils.SUITE_DIAMONDS
                R.id.switch_to_hearts -> CardUtils.SUITE_HEARTS
                R.id.switch_to_spades -> CardUtils.SUITE_SPADES
                else -> throw IllegalStateException("this listener should only be used to pick a Card suite")
            }
            CardMoves.changeSuite(suite)
            mGameRef.setValue(CardMoves.game)
        }
        btnClubs.setOnClickListener(suiteListener)
        btnDiamonds.setOnClickListener(suiteListener)
        btnHearts.setOnClickListener(suiteListener)
        btnSpades.setOnClickListener(suiteListener)
    }

    override fun onDestroy() {
        mGameRef.removeEventListener(gameUpdateListener)
        super.onDestroy()
    }

    private val gameUpdateListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val game = snapshot.getValue(Game::class.java)
            game?.let { updateStateFromGame(it) }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStateFromGame(game: Game) {

        //firebase becomes single source of truth
        CardMoves.game = game
        if (game.state === GameState.Finished) {
            startGameOverScreen()
            return
        }

        // buttons
        val pickSuiteVisibility = if (CardMoves.canPickSuite()) View.VISIBLE else View.INVISIBLE
        btnClubs.visibility = pickSuiteVisibility
        btnDiamonds.visibility = pickSuiteVisibility
        btnHearts.visibility = pickSuiteVisibility
        btnSpades.visibility = pickSuiteVisibility
        btnTakeCards.isEnabled = CardMoves.canTakeCards()
        btnDone.isEnabled = CardMoves.canEndTurn()
        if (game.owedCards > 0 && game.playersTurn == localPlayerIndex) {
            btnTakeCards.text = getString(R.string.draw_n_cards, game.owedCards)
        } else {
            btnTakeCards.text = getString(R.string.draw_card)
        }
        if (game.activeSkipTurns > 0 && game.playerToSkipTurn == localPlayerIndex) {
            btnDone.text = if (game.activeSkipTurns > 1) getString(
                R.string.skip_n_turns,
                game.activeSkipTurns
            ) else getString(R.string.skip_1_turn)
        } else {
            btnDone.setText(R.string.end_turn)
        }
        if (game.suiteOverride.isEmpty()) {
            imgSuiteOverride.visibility = View.GONE
        } else {
            imgSuiteOverride.visibility = View.VISIBLE
            imgSuiteOverride.setImageResource(CardUtils.suiteToImageId(game.suiteOverride, this))
        }

        // top of pile
        imgTopCard.setImageResource(cardToImageId(CardMoves.topCard, this))

        // cards in hand
        val cards: List<Int> = CardMoves.localPlayerCards()
        bottomCardsAdaptor.setOwnCards(cards)
        val opponentCardsCount: Int = CardMoves.opponentCardsCount
        val opponentName: String = CardMoves.opponentName
        if (opponentCardsCount >= 0) {
            txtOpponentCardsCount.text =
                getString(R.string.opponent_cards_count, opponentName, opponentCardsCount)
            opponentCardsAdapter.setCards(opponentCardsCount)
        } else {
            txtOpponentCardsCount.setText(R.string.waiting_for_other_player)
        }
        txtOpponentCardsCount.isEnabled = game.playersTurn != localPlayerIndex
    }

    private fun startGameOverScreen() {
        mGameRef.removeValue()
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra(Constants.INTENT_EXTRA_GAME, CardMoves.game)
        intent.putExtra(Constants.INTENT_EXTRA_CURRENT_PLAYER, localPlayerIndex)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}