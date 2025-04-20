package com.example.tank2d.presentation.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tank {
    private int tankX;
    private int tankY;
    private Bitmap tankImage;
    private Bitmap tankUp, tankDown, tankLeft, tankRight;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private long lastFireTime;
    private boolean alive = true;

    public Bitmap getTankImage() { return tankImage; }
    public void setTankImage(Bitmap tankImage) { this.tankImage = tankImage; }
    public int getTankX() { return tankX; }
    public void setTankX(int tankX) { this.tankX = tankX; }
    public int getTankY() { return tankY; }
    public void setTankY(int tankY) { this.tankY = tankY; }
    public Bitmap getTankDown() { return tankDown; }
    public void setTankDown(Bitmap tankDown) { this.tankDown = tankDown; }
    public Bitmap getTankLeft() { return tankLeft; }
    public void setTankLeft(Bitmap tankLeft) { this.tankLeft = tankLeft; }
    public Bitmap getTankRight() { return tankRight; }
    public void setTankRight(Bitmap tankRight) { this.tankRight = tankRight; }
    public Bitmap getTankUp() { return tankUp; }
    public void setTankUp(Bitmap tankUp) { this.tankUp = tankUp; }
    public ArrayList<Bullet> getBullets() { return bullets; }
    public boolean isAlive() { return alive; }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Tank(Context context, int tankX, int tankY, Bitmap tankUp, Bitmap tankDown,
                Bitmap tankLeft, Bitmap tankRight) {
        this.tankX = tankX;
        this.tankY = tankY;
        this.tankUp = tankUp;
        this.tankDown = tankDown;
        this.tankLeft = tankLeft;
        this.tankRight = tankRight;
        this.tankImage = tankUp;
        this.lastFireTime = 0;
    }

    public void draw(Canvas canvas) {
        if (alive) {
            canvas.drawBitmap(tankImage, tankX, tankY, null);
        }
    }

    public void moveTank(int deltaX, int deltaY, CollisionHandler collisionHandler) {
        if (!alive) return;
        int newX = tankX + deltaX;
        int newY = tankY + deltaY;

        if (collisionHandler.checkTankCollision(newX, newY) ||
                newX < 0 || newX > GameConstants.TILE_SIZE * GameConstants.GRID_WIDTH ||
                newY < 0 || newY > GameConstants.TILE_SIZE * GameConstants.GRID_HEIGHT) {
            if (deltaX > 0) tankImage = tankRight;
            else if (deltaX < 0) tankImage = tankLeft;
            else if (deltaY > 0) tankImage = tankDown;
            else if (deltaY < 0) tankImage = tankUp;
        } else {
            setTankX(tankX += deltaX);
            setTankY(tankY += deltaY);
            if (deltaX > 0) tankImage = tankRight;
            else if (deltaX < 0) tankImage = tankLeft;
            else if (deltaY > 0) tankImage = tankDown;
            else if (deltaY < 0) tankImage = tankUp;
        }
    }

    public boolean canFire() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastFireTime >= GameConstants.FIRE_DELAY &&
                bullets.size() < GameConstants.MAX_BULLETS && alive);
    }

    public void fire() {
        if (canFire()) {
            Bullet newBullet = new Bullet();
            if (tankImage == tankUp) {
                newBullet.setX(tankX + 40);
                newBullet.setY(tankY);
                newBullet.setDirection("up");
            } else if (tankImage == tankDown) {
                newBullet.setX(tankX + 40);
                newBullet.setY(tankY + 104);
                newBullet.setDirection("down");
            } else if (tankImage == tankLeft) {
                newBullet.setX(tankX);
                newBullet.setY(tankY + 40);
                newBullet.setDirection("left");
            } else if (tankImage == tankRight) {
                newBullet.setX(tankX + 104);
                newBullet.setY(tankY + 45);
                newBullet.setDirection("right");
            }
            bullets.add(newBullet);
            lastFireTime = System.currentTimeMillis();
        }
    }

    public List<Bullet> moveBullets(CollisionHandler collisionHandler) {
        List<Bullet> bulletsToRemove = new ArrayList<>();
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move(bullet.getDirection());

            // Kiểm tra đạn ra khỏi màn hình hoặc va chạm
            if (bullet.getX() < 0 || bullet.getX() > GameConstants.TILE_SIZE * GameConstants.GRID_WIDTH ||
                    bullet.getY() < 0 || bullet.getY() > GameConstants.TILE_SIZE * GameConstants.GRID_HEIGHT ||
                    collisionHandler.checkBulletCollision(bullet.getX(), bullet.getY())) {
                bulletsToRemove.add(bullet);
            }
        }
        return bulletsToRemove;
    }

    public void recycleBitmaps() {
        if (tankUp != null && !tankUp.isRecycled()) tankUp.recycle();
        if (tankDown != null && !tankDown.isRecycled()) tankDown.recycle();
        if (tankLeft != null && !tankLeft.isRecycled()) tankLeft.recycle();
        if (tankRight != null && !tankRight.isRecycled()) tankRight.recycle();
        if (tankImage != null && !tankImage.isRecycled()) tankImage.recycle();
    }
}
