package com.example.tank2d.presentation.controller;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ViewPlayervsPlayer extends View implements CollisionHandler {
    private Context context;
    private Tank tank1, tank2;
    private Brick2Player brick;
    private Paint paint;
    //    private List<Explosion> explosions = new ArrayList<>();
    private Handler bulletHandler = new Handler();
    private Runnable bulletRunnable;

    private float joystickX1 = GameConstants.TILE_SIZE * 23;
    private float joystickY1 = GameConstants.TILE_SIZE * 9 - 30;
    private float joystickX2 = GameConstants.TILE_SIZE * 3;
    private float joystickY2 = GameConstants.TILE_SIZE * 9 - 30;
    private float buttonRadius = GameConstants.JOYSTICK_RADIUS;
    private float buttonSpacing = GameConstants.JOYSTICK_SPACING;
    private boolean movingUp1, movingDown1, movingLeft1, movingRight1, movingUp2, movingDown2, movingLeft2, movingRight2;
    private int moveSpeed = GameConstants.TANK_SPEED;

    private Paint buttonPaint;
    private Paint borderPaint;
    private Paint activePaint;

    public Tank getTank1() { return tank1; }
    public void setTank1(Tank tank1) { this.tank1 = tank1; }
    public Tank getTank2() { return tank2; }
    public void setTank2(Tank tank2) { this.tank2 = tank2; }
    public Brick2Player getBrick() { return brick; }
    public void setBrick(Brick2Player brick) { this.brick = brick; }

    public ViewPlayervsPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        brick = new Brick2Player(context);
        tank1 = new Tank(context, GameConstants.TILE_SIZE * 19, GameConstants.TILE_SIZE * 8,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_up),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_down),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_left),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player1_tank_right));
        tank2 = new Tank(context, GameConstants.TILE_SIZE * 3, GameConstants.TILE_SIZE * 6,
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player2_tank_up),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player2_tank_down),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player2_tank_left),
                BitmapFactory.decodeResource(context.getResources(), R.drawable.player2_tank_right));
        paint = new Paint();

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
                if (movingUp1) tank1.moveTank(0, -moveSpeed, this);
                if (movingDown1) tank1.moveTank(0, moveSpeed, this);
                if (movingLeft1) tank1.moveTank(-moveSpeed, 0, this);
                if (movingRight1) tank1.moveTank(moveSpeed, 0, this);
                postInvalidate();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                if (movingUp2) tank2.moveTank(0, -moveSpeed, this);
                if (movingDown2) tank2.moveTank(0, moveSpeed, this);
                if (movingLeft2) tank2.moveTank(-moveSpeed, 0, this);
                if (movingRight2) tank2.moveTank(moveSpeed, 0, this);
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
                        if (tank2.isAlive() && isBulletHitTank(bullet, tank2)) {
                            tank2.setAlive(false);
                            bulletsToRemove.add(bullet);
                            break;
                        }

                    }
                    tank1.getBullets().removeAll(bulletsToRemove);
                }

                if (tank2.isAlive()) {
                    List<Bullet> bulletsToRemove = new ArrayList<>();
                    for (Bullet bullet : tank2.getBullets()) {
                        bullet.move(bullet.getDirection());
                        if (bullet.getX() < 0 || bullet.getX() > GameConstants.TILE_SIZE * GameConstants.GRID_WIDTH ||
                                bullet.getY() < 0 || bullet.getY() > GameConstants.TILE_SIZE * GameConstants.GRID_HEIGHT ||
                                checkBulletCollision(bullet.getX(), bullet.getY())) {
                            bulletsToRemove.add(bullet);
                            continue;
                        }
                        if (tank1.isAlive() && isBulletHitTank(bullet, tank1)) {
                            tank1.setAlive(false);
                            bulletsToRemove.add(bullet);
                            break;
                        }

                    }
                    tank2.getBullets().removeAll(bulletsToRemove);
                }
                invalidate();
                // Player2 win
                if (!tank1.isAlive()) {
                    bulletHandler.removeCallbacks(this); // Dừng update đạn
                    Intent intent = new Intent(context, WhoWin.class);
                    intent.putExtra("player", "Player 1");
                    context.startActivity(intent);
                    if (context instanceof MapPlayerBot) {
                        ((MapPlayerBot) context).finish();
                    }
                    return;
                }
                // Player1 win
                if (!tank2.isAlive()) {
                    bulletHandler.removeCallbacks(this); // Dừng update đạn
                    Intent intent = new Intent(context, WhoWin.class);
                    intent.putExtra("player", "Player 2");
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
        canvas.drawRect(0, 0, GameConstants.TILE_SIZE * 26, GameConstants.TILE_SIZE * 12, paint);

        drawJoystickButton(canvas, joystickX1, joystickY1 - buttonSpacing, movingUp1);
        drawJoystickButton(canvas, joystickX1, joystickY1 + buttonSpacing, movingDown1);
        drawJoystickButton(canvas, joystickX1 - buttonSpacing, joystickY1, movingLeft1);
        drawJoystickButton(canvas, joystickX1 + buttonSpacing, joystickY1, movingRight1);

        drawJoystickButton(canvas, joystickX2, joystickY2 - buttonSpacing, movingUp2);
        drawJoystickButton(canvas, joystickX2, joystickY2 + buttonSpacing, movingDown2);
        drawJoystickButton(canvas, joystickX2 - buttonSpacing, joystickY2, movingLeft2);
        drawJoystickButton(canvas, joystickX2 + buttonSpacing, joystickY2, movingRight2);

        brick.draw(canvas);
        brick.drawSolids(canvas);

        tank1.draw(canvas);
        tank2.draw(canvas);

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        for (Bullet b : tank1.getBullets()) {
            b.draw(canvas, paint);
        }

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        for (Bullet b : tank2.getBullets()) {
            b.draw(canvas, paint);
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
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        // Reset trạng thái
        movingUp1 = movingDown1 = movingLeft1 = movingRight1 = false;
        movingUp2 = movingDown2 = movingLeft2 = movingRight2 = false;

        for (int i = 0; i < pointerCount; i++) {
            float x = event.getX(i) / 0.6f;
            float y = event.getY(i) / 0.6f;

            if (isInCircle(x, y, joystickX1, joystickY1 - buttonSpacing, buttonRadius)) {
                movingUp1 = true;
            } else if (isInCircle(x, y, joystickX1, joystickY1 + buttonSpacing, buttonRadius)) {
                movingDown1 = true;
            } else if (isInCircle(x, y, joystickX1 - buttonSpacing, joystickY1, buttonRadius)) {
                movingLeft1 = true;
            } else if (isInCircle(x, y, joystickX1 + buttonSpacing, joystickY1, buttonRadius)) {
                movingRight1 = true;
            }

            if (isInCircle(x, y, joystickX2, joystickY2 - buttonSpacing, buttonRadius)) {
                movingUp2 = true;
            } else if (isInCircle(x, y, joystickX2, joystickY2 + buttonSpacing, buttonRadius)) {
                movingDown2 = true;
            } else if (isInCircle(x, y, joystickX2 - buttonSpacing, joystickY2, buttonRadius)) {
                movingLeft2 = true;
            } else if (isInCircle(x, y, joystickX2 + buttonSpacing, joystickY2, buttonRadius)) {
                movingRight2 = true;
            }
        }

        // Khi tất cả ngón tay rời khỏi màn hình thì reset
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            movingUp1 = movingDown1 = movingLeft1 = movingRight1 = false;
            movingUp2 = movingDown2 = movingLeft2 = movingRight2 = false;
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
        tank1.recycleBitmaps();
        brick.recycleBitmaps();
    }
}
