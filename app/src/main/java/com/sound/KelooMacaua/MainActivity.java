package com.sound.KelooMacaua;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView player2Cards;
    ImageView tablePile;


    public void calculate(View view){
        //EditText editText = (EditText) findViewById(R.id.editTextNumber);
        //Integer inputNo = Integer.parseInt(editText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardMoves cardMoves = CardMoves.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player2Cards = findViewById(R.id.player2Cards);
        tablePile = findViewById(R.id.tablePile);

        player2Cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tablePile.setImageResource(R.drawable.ace_of_clubs);
                player2Cards.setVisibility(View.GONE);
            }
        });

    }
}