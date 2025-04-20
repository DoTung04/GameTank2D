package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tank2d.MainActivity;
import com.example.tank2d.R;

public class LoadingGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_game);

        // Chạy màn hình loading trong 3 giây
        new Handler().postDelayed(() -> {
            // Chuyển sang màn hình Menu chính
            Intent intent = new Intent(LoadingGame.this, MenuGame.class);
            startActivity(intent);
            finish(); // Đóng Activity hiện tại
        }, 5000); // 3000ms = 3s
    }
}