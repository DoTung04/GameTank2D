package com.example.tank2d.presentation.controller;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.tank2d.R;
import com.example.tank2d.presentation.controller.GameWin;
import com.example.tank2d.presentation.controller.GameOver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewPlayerBot extends View implements CollisionHandler {
    private Context context;
    private List<Bot> bots;
    private Tank tank1;
    private Brick brick;
    private Paint paint;
    private Bitmap botIcon;
    private List<Explosion> explosions = new ArrayList<>();
    private Handler bulletHandler = new Handler();
    private Runnable bulletRunnable;

    private float joystickX = GameConstants.TILE_SIZE * 23;
    private float joystickY = GameConstants.TILE_SIZE * 9 - 30;
    private float buttonRadius = GameConstants.JOYSTICK_RADIUS;
    private float buttonSpacing = GameConstants.JOYSTICK_SPACING;
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private int moveSpeed = GameConstants.TANK_SPEED;

    private Paint buttonPaint;
    private Paint borderPaint;
    private Paint activePaint;

    private class Explosion {
        float x, y;
        float radius;
        int alpha;

        Explosion(float x, float y) {
            this.x = x;
            this.y = y;
            this.radius = 0;
            this.alpha = 255;
        }

        void update() {
            radius += 10;
            alpha -= 15;
        }

        void draw(Canvas canvas, Paint paint) {
            if (alpha > 0) {
                paint.setAlpha(alpha);
                canvas.drawCircle(x, y, radius, paint);
                paint.setAlpha(255);
            }
        }
    }

    public Tank getTank1() { return tank1; }
    public List<Bot> getBots() { return bots; }
    public void setBots(List<Bot> bots) { this.bots = bots; }
    public void setTank1(Tank tank1) { this.tank1 = tank1; }
    public Brick getBrick() { return brick; }
    public void setBrick(Brick brick) { this.brick = brick; }

    public ViewPlayerBot(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        brick = new Brick(context);
        tank1 = new Tank(context, GameConstants.TILE_SIZE * 6, GameConstants.TILE_SIZE * 8,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_up),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_down),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_left),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_right));

        bots = new ArrayList<>();
        int[][] botPositions = {
                {GameConstants.TILE_SIZE * 1, GameConstants.TILE_SIZE * 1},
                {GameConstants.TILE_SIZE * 9, GameConstants.TILE_SIZE * 2},
                {GameConstants.TILE_SIZE * 15, GameConstants.TILE_SIZE * 4},
                {GameConstants.TILE_SIZE * 20, GameConstants.TILE_SIZE * 3},
                {GameConstants.TILE_SIZE * 8, GameConstants.TILE_SIZE * 6},
                {GameConstants.TILE_SIZE * 13, GameConstants.TILE_SIZE * 5},
                {GameConstants.TILE_SIZE * 18, GameConstants.TILE_SIZE * 7},
                {GameConstants.TILE_SIZE * 20, GameConstants.TILE_SIZE * 6},
                {GameConstants.TILE_SIZE * 1, GameConstants.TILE_SIZE * 3},
                {GameConstants.TILE_SIZE * 1, GameConstants.TILE_SIZE * 4},
                {GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 3},
                {GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 6}
        };

        for (int i = 0; i < botPositions.length; i++) {
            bots.add(new Bot(context, botPositions[i][0], botPositions[i][1],
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.bot_tank_up),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.bot_tank_down),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.bot_tank_left),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.bot_tank_right),
                    tank1, brick, this));
        }

        for (Bot bot : bots) {
            bot.setOtherBots(bots);
        }

        paint = new Paint();
        botIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.bot_tank_up);

        buttonPaint = new Paint();
        buttonPaint.setStyle(Paint.Style.FILL);
        buttonPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setAntiAlias(true);

        activePaint = new Paint();
        activePaint.setStyle(Paint.Style.FILL);
        activePaint.setColor(Color.YELLOW);
        activePaint.setAntiAlias(true);

        startMovement();
        startBulletUpdates();
    }

    private void startMovement() {
        new Thread(() -> {
            while (true) {
                if (movingUp) tank1.moveTank(0, -moveSpeed, this);
                if (movingDown) tank1.moveTank(0, moveSpeed, this);
                if (movingLeft) tank1.moveTank(-moveSpeed, 0, this);
                if (movingRight) tank1.moveTank(moveSpeed, 0, this);
                postInvalidate();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startBulletUpdates() {
        bulletRunnable = new Runnable() {
            @Override
            public void run() {
                // Cập nhật đạn cho tank1
                if (tank1.isAlive()) {
                    List<Bullet> bulletsToRemove = new ArrayList<>();
                    for (Bullet bullet : tank1.getBullets()) {
                        bullet.move(bullet.getDirection());
                        if (bullet.getX() < 0 || bullet.getX() > GameConstants.TILE_SIZE * GameConstants.GRID_WIDTH ||
                                bullet.getY() < 0 || bullet.getY() > GameConstants.TILE_SIZE * GameConstants.GRID_HEIGHT ||
                                checkBulletCollision(bullet.getX(), bullet.getY())) {
                            bulletsToRemove.add(bullet);
                            continue;
                        }
                        // Kiểm tra va chạm với bot
                        for (Bot bot : bots) {
                            if (bot.isAlive() && isBulletHitTank(bullet, bot)) {
                                bot.setAlive(false);
                                explosions.add(new Explosion(bot.getTankX() + GameConstants.TILE_SIZE / 2,
                                        bot.getTankY() + GameConstants.TILE_SIZE / 2));
                                bulletsToRemove.add(bullet);
                                break;
                            }
                        }
                    }
                    tank1.getBullets().removeAll(bulletsToRemove);
                }

                // Cập nhật đạn cho các bot
                for (Bot bot : bots) {
                    if (bot.isAlive()) {
                        List<Bullet> bulletsToRemove = new ArrayList<>();
                        for (Bullet bullet : bot.getBullets()) {
                            bullet.move(bullet.getDirection());
                            if (bullet.getX() < 0 || bullet.getX() > GameConstants.TILE_SIZE * GameConstants.GRID_WIDTH ||
                                    bullet.getY() < 0 || bullet.getY() > GameConstants.TILE_SIZE * GameConstants.GRID_HEIGHT ||
                                    checkBulletCollision(bullet.getX(), bullet.getY())) {
                                bulletsToRemove.add(bullet);
                                continue;
                            }
                            // Kiểm tra va chạm với tank1
                            if (tank1.isAlive() && isBulletHitTank(bullet, tank1)) {
                                tank1.setAlive(false);
                                explosions.add(new Explosion(tank1.getTankX() + GameConstants.TILE_SIZE / 2,
                                        tank1.getTankY() + GameConstants.TILE_SIZE / 2));
                                bulletsToRemove.add(bullet);
                            }
                        }
                        bot.getBullets().removeAll(bulletsToRemove);
                    }
                }

                invalidate();
                // Kiểm tra win
                boolean allBotsDead = true;
                for (Bot bot : bots) {
                    if (bot.isAlive()) {
                        allBotsDead = false;
                        break;
                    }
                }
                if (allBotsDead) {
                    bulletHandler.removeCallbacks(this); // Dừng update đạn
                    Intent intent = new Intent(context, GameWin.class);
                    context.startActivity(intent);
                    if (context instanceof MapPlayerBot) {
                        ((MapPlayerBot) context).finish();
                    }
                    return;
                }

                // Kiểm tra lose
                if (!tank1.isAlive()) {
                    bulletHandler.removeCallbacks(this); // Dừng update đạn
                    Intent intent = new Intent(context, GameOver.class);
                    context.startActivity(intent);
                    if (context instanceof MapPlayerBot) {
                        ((MapPlayerBot) context).finish();
                    }
                    return;
                }

                bulletHandler.postDelayed(this, 50);
            }
        };
        bulletHandler.post(bulletRunnable);
    }

    // Thêm phương thức kiểm tra va chạm đạn với tank
    private boolean isBulletHitTank(Bullet bullet, Tank tank) {
        Rect bulletRect = new Rect(bullet.getX(), bullet.getY(), bullet.getX() + 26, bullet.getY() + 26);
        Rect tankRect = new Rect(tank.getTankX(), tank.getTankY(),
                tank.getTankX() + GameConstants.TILE_SIZE, tank.getTankY() + GameConstants.TILE_SIZE);
        return bulletRect.intersect(tankRect);
    }

    @Override
    public boolean checkBulletCollision(int bulletX, int bulletY) {
        return brick.checkCollision(bulletX, bulletY) || brick.checkSolidCollision(bulletX, bulletY);
    }

    @Override
    public boolean checkTankCollision(int tankX, int tankY) {
        return brick.checkTankCollision(tankX, tankY) || brick.checkTankSolidCollision(tankX, tankY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.scale(0.6f, 0.6f);

        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, GameConstants.TILE_SIZE * 21, GameConstants.TILE_SIZE * 12, paint);

        paint.setColor(Color.GRAY);
        canvas.drawRect(GameConstants.TILE_SIZE * 21, 0, GameConstants.TILE_SIZE * 26, GameConstants.TILE_SIZE * 12, paint);

        drawJoystickButton(canvas, joystickX, joystickY - buttonSpacing, movingUp);
        drawJoystickButton(canvas, joystickX, joystickY + buttonSpacing, movingDown);
        drawJoystickButton(canvas, joystickX - buttonSpacing, joystickY, movingLeft);
        drawJoystickButton(canvas, joystickX + buttonSpacing, joystickY, movingRight);

        int iconSize = 75;
        for (int i = 0; i < 12; i++) {
            if (i < bots.size() && bots.get(i).isAlive()) {
                int row = 5 - (i / 2);
                int x = GameConstants.TILE_SIZE * 21 + (i % 4) * iconSize + 40;
                int y = GameConstants.TILE_SIZE * (row/2) + 20;
                canvas.drawBitmap(botIcon, x, y, null);
            }
        }

        int aliveBots = 0;
        for (Bot bot : bots) {
            if (bot.isAlive()) aliveBots++;
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        String botsText = "Bots: " + aliveBots;
        canvas.drawText(botsText, GameConstants.TILE_SIZE * 22.5f + 75, GameConstants.TILE_SIZE * 5.5f, paint);

        brick.draw(canvas);
        brick.drawSolids(canvas);

        tank1.draw(canvas);

        for (Bot bot : bots) {
            bot.draw(canvas);
        }

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        for (Bullet b : tank1.getBullets()) {
            b.draw(canvas, paint);
        }

        paint.setColor(Color.RED);
        for (Bot bot : bots) {
            if (bot.isAlive()) {
                for (Bullet b : bot.getBullets()) {
                    b.draw(canvas, paint);
                }
            }
        }

        paint.setColor(Color.BLUE);
        for (Iterator<Explosion> it = explosions.iterator(); it.hasNext(); ) {
            Explosion exp = it.next();
            exp.update();
            exp.draw(canvas, paint);
            if (exp.alpha <= 0) it.remove();
        }
    }

    private void drawJoystickButton(Canvas canvas, float x, float y, boolean isActive) {
        LinearGradient gradient = new LinearGradient(
                x, y - buttonRadius, x, y + buttonRadius,
                Color.parseColor("#00CED1"), Color.parseColor("#006400"),
                Shader.TileMode.CLAMP);
        buttonPaint.setShader(gradient);

        if (isActive) {
            canvas.drawCircle(x, y, buttonRadius, activePaint);
        } else {
            canvas.drawCircle(x, y, buttonRadius, buttonPaint);
        }

        canvas.drawCircle(x, y, buttonRadius, borderPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() / 0.6f;
        float y = event.getY() / 0.6f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                movingUp = movingDown = movingLeft = movingRight = false;

                if (isInCircle(x, y, joystickX, joystickY - buttonSpacing, buttonRadius)) {
                    movingUp = true;
                } else if (isInCircle(x, y, joystickX, joystickY + buttonSpacing, buttonRadius)) {
                    movingDown = true;
                } else if (isInCircle(x, y, joystickX - buttonSpacing, joystickY, buttonRadius)) {
                    movingLeft = true;
                } else if (isInCircle(x, y, joystickX + buttonSpacing, joystickY, buttonRadius)) {
                    movingRight = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                movingUp = movingDown = movingLeft = movingRight = false;
                break;
        }
        invalidate();
        return true;
    }

    private boolean isInCircle(float x, float y, float centerX, float centerY, float radius) {
        float dx = x - centerX;
        float dy = y - centerY;
        return dx * dx + dy * dy <= radius * radius;
    }

    public void cleanup() {
        bulletHandler.removeCallbacksAndMessages(null);
        for (Bot bot : bots) {
            bot.recycleBitmaps();
        }
        tank1.recycleBitmaps();
        brick.recycleBitmaps();
        if (botIcon != null && !botIcon.isRecycled()) {
            botIcon.recycle();
        }
    }
}
