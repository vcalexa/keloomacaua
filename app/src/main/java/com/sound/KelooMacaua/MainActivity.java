package com.sound.KelooMacaua;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.KelooMacaua.adaptors.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView player2Cards;
    ImageView tablePile;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter bottomCardsAdaptor;
    List<ActualCard> cardList = new ArrayList<>();

    public void calculate(View view) {
        //EditText editText = (EditText) findViewById(R.id.editTextNumber);
        //Integer inputNo = Integer.parseInt(editText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardMoves cardMoves = CardMoves.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Card card = new Card();
        ActualCard card1 = new ActualCard(1, String.format("%s_of_%s", "two", card.getCardSuite(1)).toLowerCase());
        ActualCard card2 = new ActualCard(2, String.format("%s_of_%s", "three", card.getCardSuite(2)).toLowerCase());
        ActualCard card3 = new ActualCard(3, String.format("%s_of_%s", "ace", card.getCardSuite(3)).toLowerCase());
        ActualCard card4 = new ActualCard(4, String.format("%s_of_%s", "king", card.getCardSuite(4)).toLowerCase());
        ActualCard card5 = new ActualCard(5, String.format("%s_of_%s", "queen", card.getCardSuite(5)).toLowerCase());

        cardList.add(card1);
        cardList.add(card2);
        cardList.add(card3);
        cardList.add(card4);
        cardList.add(card5);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = findViewById(R.id.recycleViewCards);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        bottomCardsAdaptor = new MyAdapter(getApplicationContext(), cardList);
        recyclerView.setAdapter(bottomCardsAdaptor);

        /*player2Cards = findViewById(R.id.player2Cards);
        tablePile = findViewById(R.id.tablePile);

        player2Cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tablePile.setImageResource(R.drawable.ace_of_clubs);
                player2Cards.setVisibility(View.GONE);
            }
        });*/

        /*Random rand = new Random();

        LinearLayout layout = findViewById(R.id.layout_bottom);
        for (int i = 0; i < 5; i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(100, 330));
            image.setMaxHeight(170);
            image.setMaxWidth(170);

            int n = rand.nextInt(53) + 1;
            Card cardx = new Card();
            int imageId = getResources().getIdentifier(String.format("%s_of_%s", cardx.getCardRank(n), cardx.getCardSuite(n)).toLowerCase(),
                    "drawable", getPackageName());
            //image.setImageResource(imageId);
            // Adds the view to the layout
            //layout.addView(image);
        }*/


    }
}