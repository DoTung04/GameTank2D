package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tank2d.R;

public class MapPlayervsPlayer extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    private ViewPlayervsPlayer viewPlayervsPlayer;
    private ImageButton btnFire1;
    private ImageButton btnFire2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_playervs_player);

        // Tham chiếu ViewPlayervsPlayer
        viewPlayervsPlayer = findViewById(R.id.viewPlayervsPlayer);

        // Tham chiếu các nút bắn
        btnFire1 = findViewById(R.id.btnFire1);
        btnFire2 = findViewById(R.id.btnFire2);

        Intent intent = getIntent();
        boolean isMusicGunOn = intent.getBooleanExtra("musicgun", false);

        // Gán sự kiện nhấn cho nút bắn
        btnFire1.setOnClickListener(v -> {
            if (viewPlayervsPlayer.getTank1() != null && viewPlayervsPlayer.getTank1().canFire()) {
                if (isMusicGunOn) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release(); // Giải phóng trước nếu có
                    }
                    mediaPlayer = MediaPlayer.create(this, R.raw.soundshoot);
                    mediaPlayer.start(); // Phát nhạc
                }
                viewPlayervsPlayer.setIsMusicGunOn(isMusicGunOn);
                viewPlayervsPlayer.getTank1().fire();
                viewPlayervsPlayer.invalidate();
            }
        });

        btnFire2.setOnClickListener(v -> {
            if (viewPlayervsPlayer.getTank2() != null && viewPlayervsPlayer.getTank2().canFire()) {
                if (isMusicGunOn) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release(); // Giải phóng trước nếu có
                    }
                    mediaPlayer = MediaPlayer.create(this, R.raw.soundshoot);
                    mediaPlayer.start(); // Phát nhạc
                }
                viewPlayervsPlayer.getTank2().fire();
                viewPlayervsPlayer.invalidate();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewPlayervsPlayer != null) {
            viewPlayervsPlayer.cleanup();
        }
    }
}