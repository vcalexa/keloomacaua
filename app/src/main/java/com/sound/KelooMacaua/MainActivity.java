package com.sound.KelooMacaua;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.KelooMacaua.adaptors.MyAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView player2Cards;
    ImageView tableCard;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapter bottomCardsAdaptor;

    Button iaCarteButton;

    public void calculate(View view) {
        //EditText editText = (EditText) findViewById(R.id.editTextNumber);
        //Integer inputNo = Integer.parseInt(editText.getText().toString());
    }

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
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        bottomCardsAdaptor = new MyAdapter(getApplicationContext(), player1Cards, tableCard);
        recyclerView.setAdapter(bottomCardsAdaptor);

        iaCarteButton = findViewById(R.id.iaCarteId);
        iaCarteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardMoves.player1Takes(1);
                recyclerView.setAdapter(bottomCardsAdaptor);
            }
        });

        // player2Cards = findViewById(R.id.player2Cards);


       // tablePile.setImageResource(R.drawable.ace_of_clubs);


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