package com.sound.KelooMacaua;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView player2Cards;
    ImageView tablePile;


    public void calculate(View view) {
        //EditText editText = (EditText) findViewById(R.id.editTextNumber);
        //Integer inputNo = Integer.parseInt(editText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardMoves cardMoves = CardMoves.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*player2Cards = findViewById(R.id.player2Cards);
        tablePile = findViewById(R.id.tablePile);

        player2Cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tablePile.setImageResource(R.drawable.ace_of_clubs);
                player2Cards.setVisibility(View.GONE);
            }
        });*/

        Random r = new Random();

        LinearLayout layout = findViewById(R.id.imageLayout);
        for (int i = 0; i < 10; i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(100, 100));
            image.setMaxHeight(170);
            image.setMaxWidth(170);

            int n = r.nextInt(53) + 1;
            Card card = new Card();
            int imageId = getResources().getIdentifier(String.format("%s_of_%s", card.getCardRank(n), card.getCardSuite(n)).toLowerCase(),
                    "drawable", getPackageName());
            image.setImageResource(imageId);
            // Adds the view to the layout
            layout.addView(image);
        }

    }
}