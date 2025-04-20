package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tank2d.R;

public class MenuGame extends AppCompatActivity {
    Button btnSetting,btnPlayWithFriend, btnPlayWithAI, btnHelp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_game);

        btnSetting = findViewById(R.id.btnSetting);
        btnPlayWithAI = findViewById(R.id.btnPlayWithAI);
        btnPlayWithFriend = findViewById(R.id.btnPlayWithFriend);
        btnHelp = findViewById(R.id.btnHelp);

        btnSetting.setOnClickListener(v -> {
            // Add intent to Setting Activity
            Intent intent = new Intent(MenuGame.this, Setting.class);
            startActivity(intent);
        });

        if (btnPlayWithAI != null) {
            btnPlayWithAI.setOnClickListener(v -> {
                // Lấy trạng thái đã lưu từ SharedPreferences
                boolean isMusicGunOn = getSharedPreferences("AppSettings", MODE_PRIVATE)
                        .getBoolean("musicGunOn", false);
                Intent intent = new Intent(MenuGame.this, MapPlayerBot.class);
                intent.putExtra("musicgun", isMusicGunOn);
                startActivity(intent);
            });
        }

        if (btnPlayWithFriend != null) {
            btnPlayWithFriend.setOnClickListener(v -> {
                // Lấy trạng thái đã lưu từ SharedPreferences
                boolean isMusicGunOn = getSharedPreferences("AppSettings", MODE_PRIVATE)
                        .getBoolean("musicGunOn", false);
                Intent intent = new Intent(MenuGame.this, MapPlayervsPlayer.class);
                intent.putExtra("musicgun", isMusicGunOn);
                startActivity(intent);
            });
        }

        btnHelp.setOnClickListener(v -> {
            showGameRulesDialog();
        });
    }
    private void showGameRulesDialog() {
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate layout tùy chỉnh
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_game_rules, null);
        builder.setView(dialogView);

        // Nút OK để đóng dialog
        //builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        // Hiển thị dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
}