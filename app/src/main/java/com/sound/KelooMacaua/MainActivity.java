package com.sound.KelooMacaua;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {



    public void calculate(View view){
        //EditText editText = (EditText) findViewById(R.id.editTextNumber);
        //Integer inputNo = Integer.parseInt(editText.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CardMoves cardMoves = CardMoves.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}