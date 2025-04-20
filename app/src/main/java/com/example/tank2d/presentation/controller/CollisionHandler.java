package com.example.tank2d.presentation.controller;

public interface CollisionHandler {
    // Kiểm tra va chạm của đạn với các vật cản (gạch, tường, v.v.)
    boolean checkBulletCollision(int bulletX, int bulletY);

    // Kiểm tra va chạm của tank với các vật cản
    boolean checkTankCollision(int tankX, int tankY);
}

