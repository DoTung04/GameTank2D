package com.example.tank2d.presentation.controller;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Bullet {
    private int x, y;
    private int speed = GameConstants.BULLET_SPEED;
    private String direction;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Bullet() {}

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void draw(Canvas canvas, Paint a) {
        canvas.drawOval(new RectF(x, y, x + 52, y + 52), a);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void move(String a) {
        if (a.equals("up")) {
            setY(getY() - speed);
        } else if (a.equals("down")) {
            setY(getY() + speed);
        } else if (a.equals("left")) {
            setX(getX() - speed);
        } else {
            setX(getX() + speed);
        }
    }
}
