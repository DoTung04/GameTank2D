package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tank2d.R;

public class WhoWin extends AppCompatActivity {
    TextView tvview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who_win);
        tvview = findViewById(R.id.tvView);
        Intent intent = getIntent();
        String winner = intent.getStringExtra("player");
        tvview.setText(winner + " win!");

        // Nút Play Again
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        if (btnPlayAgain != null) {
            btnPlayAgain.setOnClickListener(v -> {
                Intent intent1 = new Intent(WhoWin.this, MapPlayervsPlayer.class);
                startActivity(intent1);
                finish();
            });
        }

        // Nút Exit
        Button btnExit = findViewById(R.id.btnExit);
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                Intent intent1 = new Intent(WhoWin.this, MenuGame.class);
                startActivity(intent1);
                finish();
            });
        }

    }
}