package com.example.whist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void shuffleCards(View view) {
        ArrayList<Integer> cards = CardShuffler.shuffleCards(4);

        Log.d("Player 1", cards.subList(0, 8).toString());
        Log.d("Player 2", cards.subList(8, 16).toString());
        Log.d("Player 3", cards.subList(16, 24).toString());
        Log.d("Player 4", cards.subList(24, 32).toString());
    }


    public void joinGame(View view) {
        Intent myIntent = new Intent(this, WaitingRoomActivity.class);
        startActivity(myIntent);
    }
}