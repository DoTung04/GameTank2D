package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tank2d.R;

public class GameOver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        Intent intent1 = getIntent();
        boolean isMusicGunOn = intent1.getBooleanExtra("musicgun", false);

        // Nút Play Again
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        if (btnPlayAgain != null) {
            btnPlayAgain.setOnClickListener(v -> {
                // Chuyển về màn hình chơi game (giả sử là GameActivity)
                Intent intent = new Intent(GameOver.this, MapPlayerBot.class); // Thay GameActivity bằng activity chơi game của bạn
                intent.putExtra("musicgun", isMusicGunOn);
                startActivity(intent);
                finish();
            });
        }

        // Nút Exit
        Button btnExit = findViewById(R.id.btnExit);
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                // Quay về MenuGame
                Intent intent = new Intent(GameOver.this, MenuGame.class);
                startActivity(intent);
                finish();
            });
        }
    }
}