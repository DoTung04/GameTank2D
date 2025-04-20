package com.example.tank2d.presentation.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tank2d.R;

import java.util.Set;

public class Setting extends AppCompatActivity {
    boolean isMusicOn, isMusicGunOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Switch switchMusic = findViewById(R.id.switchMusic);
        Switch switchMusicGun = findViewById(R.id.switchMusicGun);
        Button btnOk = findViewById(R.id.btnOk);

        // Khôi phục trạng thái từ SharedPreferences
        final SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean savedMusicOn = prefs.getBoolean("musicOn", false);
        boolean savedMusicGunOn = prefs.getBoolean("musicGunOn", false);

        switchMusic.setChecked(savedMusicOn);
        switchMusicGun.setChecked(savedMusicGunOn);

        btnOk.setOnClickListener(v -> {
            boolean newMusicOn = switchMusic.isChecked();
            boolean newMusicGunOn = switchMusicGun.isChecked();

            // Lưu nếu có thay đổi
            prefs.edit()
                    .putBoolean("musicOn", newMusicOn)
                    .putBoolean("musicGunOn", newMusicGunOn)
                    .apply();

            if (newMusicOn != savedMusicOn) {
                if (newMusicOn) {
                    startService(new Intent(this, BackgroundMusic.class));
                } else {
                    stopService(new Intent(this, BackgroundMusic.class));
                }
            }

            android.util.Log.d("SettingActivity", "Music: " + newMusicOn + ", Music Gun: " + newMusicGunOn);
            finish();
        });
    }

}