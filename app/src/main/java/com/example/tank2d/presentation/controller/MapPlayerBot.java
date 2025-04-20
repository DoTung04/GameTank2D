package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tank2d.R;

public class MapPlayerBot extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    ImageButton btnFire;
    ViewPlayerBot viewPlayerBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_player_bot);

        Intent intent = getIntent();
        boolean isMusicGunOn = intent.getBooleanExtra("musicgun", false);

        btnFire = findViewById(R.id.btnFire);
        viewPlayerBot = findViewById(R.id.viewPlayerBot);

        btnFire.setOnClickListener(v -> {
            if (viewPlayerBot.getTank1().canFire()) {
                if (isMusicGunOn) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release(); // Giải phóng trước nếu có
                        mediaPlayer = null;
                    }
                    mediaPlayer = MediaPlayer.create(this, R.raw.soundshoot);

                    // Tự động giải phóng sau khi phát xong
                    mediaPlayer.setOnCompletionListener(mp -> {
                        mp.release();
                        mediaPlayer = null;
                    });

                    mediaPlayer.start();
                }
                viewPlayerBot.setIsMusicGunOn(isMusicGunOn);
                viewPlayerBot.getTank1().fire();
                viewPlayerBot.invalidate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        viewPlayerBot.cleanup();
    }
}
