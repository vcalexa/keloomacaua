package com.sound.keloomacaua.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.adaptors.MyAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView player2Cards;
    ImageView tableCard;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapter bottomCardsAdaptor;

    Button iaCarteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardMoves cardMoves = CardMoves.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Integer> player1Cards = cardMoves.getPlayer1Cards();
        List<Integer> player2Cards = cardMoves.getPlayer2Cards();

        tableCard = findViewById(R.id.tablePile);

        String firstCardTitle = cardMoves.getCardUtils().getImageViewName(cardMoves.getLast());
        int firstCardId = getResources().getIdentifier(firstCardTitle,
                "drawable", getPackageName());

        tableCard.setImageResource(firstCardId);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycleViewCards);

        recyclerView.setLayoutManager(layoutManager);
        bottomCardsAdaptor = new MyAdapter(getApplicationContext(), player1Cards, tableCard);
        recyclerView.setAdapter(bottomCardsAdaptor);

        recyclerView.scrollToPosition(player1Cards.size() - 1);

        iaCarteButton = findViewById(R.id.iaCarteId);
        iaCarteButton.setOnClickListener(view -> {
            cardMoves.player1Takes(1);
            recyclerView.setAdapter(bottomCardsAdaptor);
            recyclerView.scrollToPosition(player1Cards.size() - 1);
        });






    }
}