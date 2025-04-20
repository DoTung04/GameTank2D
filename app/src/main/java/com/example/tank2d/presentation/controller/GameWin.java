package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tank2d.R;

public class GameWin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_win);

        // Nút Play Again
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        if (btnPlayAgain != null) {
            btnPlayAgain.setOnClickListener(v -> {
                Intent intent = new Intent(GameWin.this, MapPlayerBot.class);
                startActivity(intent);
                finish();
            });
        }

        // Nút Exit
        Button btnExit = findViewById(R.id.btnExit);
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                Intent intent = new Intent(GameWin.this, MenuGame.class);
                startActivity(intent);
                finish();
            });
        }
    }
}