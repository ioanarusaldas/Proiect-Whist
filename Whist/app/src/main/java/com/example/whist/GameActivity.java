package com.example.whist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private CardShuffler cardShuffler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}