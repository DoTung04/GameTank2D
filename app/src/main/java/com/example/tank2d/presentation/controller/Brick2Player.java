package com.example.tank2d.presentation.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.example.tank2d.R;

import java.util.Arrays;

public class Brick2Player {
    int bricksXPos[] = {
            GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 3, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 10, GameConstants.TILE_SIZE * 12, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 19, GameConstants.TILE_SIZE * 19, GameConstants.TILE_SIZE * 8,
            GameConstants.TILE_SIZE * 7, GameConstants.TILE_SIZE * 7, GameConstants.TILE_SIZE * 11,
            GameConstants.TILE_SIZE * 12, GameConstants.TILE_SIZE * 13, GameConstants.TILE_SIZE * 13,
            GameConstants.TILE_SIZE * 11, GameConstants.TILE_SIZE * 13, GameConstants.TILE_SIZE * 16,
            GameConstants.TILE_SIZE * 16, GameConstants.TILE_SIZE * 17, GameConstants.TILE_SIZE * 22,
    };

    int bricksYPos[] = {
            GameConstants.TILE_SIZE * 3, GameConstants.TILE_SIZE * 0, GameConstants.TILE_SIZE * 1,
            GameConstants.TILE_SIZE * 1, GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 2,
            GameConstants.TILE_SIZE * 1, GameConstants.TILE_SIZE * 3, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 6, GameConstants.TILE_SIZE * 7, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 5, GameConstants.TILE_SIZE * 4, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 8, GameConstants.TILE_SIZE * 8, GameConstants.TILE_SIZE * 8,
            GameConstants.TILE_SIZE * 9, GameConstants.TILE_SIZE * 9, GameConstants.TILE_SIZE * 5
    };

    int solidBricksXPos[] = {
            GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 4,
            GameConstants.TILE_SIZE * 6, GameConstants.TILE_SIZE * 7,
            GameConstants.TILE_SIZE * 9, GameConstants.TILE_SIZE * 10, GameConstants.TILE_SIZE * 11,
            GameConstants.TILE_SIZE * 11, GameConstants.TILE_SIZE * 10, GameConstants.TILE_SIZE * 10,
            GameConstants.TILE_SIZE * 12, GameConstants.TILE_SIZE * 14, GameConstants.TILE_SIZE * 14,
            GameConstants.TILE_SIZE * 15, GameConstants.TILE_SIZE * 21, GameConstants.TILE_SIZE * 23,
            GameConstants.TILE_SIZE * 23, GameConstants.TILE_SIZE * 23, GameConstants.TILE_SIZE * 19
    };

    int solidBricksYPos[] = {
            GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 4, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 5, GameConstants.TILE_SIZE * 5,
            GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 2, GameConstants.TILE_SIZE * 2,
            GameConstants.TILE_SIZE * 1, GameConstants.TILE_SIZE * 7, GameConstants.TILE_SIZE * 8,
            GameConstants.TILE_SIZE * 8, GameConstants.TILE_SIZE * 7, GameConstants.TILE_SIZE * 8,
            GameConstants.TILE_SIZE * 3, GameConstants.TILE_SIZE * 5, GameConstants.TILE_SIZE * 3,
            GameConstants.TILE_SIZE * 4, GameConstants.TILE_SIZE * 5, GameConstants.TILE_SIZE * 2
    };

    int brickON[] = new int[bricksXPos.length];
    private Bitmap breakBrickImage;
    private Bitmap solidBrickImage;
    private int[][] grid = new int[GameConstants.GRID_WIDTH][GameConstants.GRID_HEIGHT];
    private boolean gridChanged = false;
    private int lastBrokenBrickX = -1; // Tọa độ lưới của gạch vừa vỡ
    private int lastBrokenBrickY = -1;

    public Brick2Player(Context context) {
        breakBrickImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.break_brick);
        solidBrickImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.solid_brick);
        for (int i = 0; i < brickON.length; i++) {
            brickON[i] = 1;
        }
        updateGrid();
    }

    public void updateGrid() {
        for (int i = 0; i < GameConstants.GRID_WIDTH; i++) {
            Arrays.fill(grid[i], 0);
        }
        for (int i = 0; i < solidBricksXPos.length; i++) {
            int bx = solidBricksXPos[i] / GameConstants.TILE_SIZE;
            int by = solidBricksYPos[i] / GameConstants.TILE_SIZE;
            if (bx < GameConstants.GRID_WIDTH && by < GameConstants.GRID_HEIGHT) {
                grid[bx][by] = 1;
            }
        }
        gridChanged = true;
    }

    public boolean hasGridChanged() {
        return gridChanged;
    }

    public void resetGridChanged() {
        gridChanged = false;
        lastBrokenBrickX = -1;
        lastBrokenBrickY = -1;
    }

    public int getLastBrokenBrickX() {
        return lastBrokenBrickX;
    }

    public int getLastBrokenBrickY() {
        return lastBrokenBrickY;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < brickON.length; i++) {
            if (brickON[i] == 1) {
                canvas.drawBitmap(breakBrickImage, bricksXPos[i], bricksYPos[i], null);
            }
        }
    }

    public void drawSolids(Canvas canvas) {
        for (int i = 0; i < solidBricksXPos.length; i++) {
            canvas.drawBitmap(solidBrickImage, solidBricksXPos[i], solidBricksYPos[i], null);
        }
    }

    public boolean checkCollision(int x, int y) {
        for (int i = 0; i < brickON.length; i++) {
            if (brickON[i] == 1) {
                Rect bulletRect = new Rect(x, y, x + 26, y + 26);
                Rect brickRect = new Rect(bricksXPos[i], bricksYPos[i], bricksXPos[i] + GameConstants.TILE_SIZE,
                        bricksYPos[i] + GameConstants.TILE_SIZE);
                if (bulletRect.intersect(brickRect)) {
                    brickON[i] = 0;
                    lastBrokenBrickX = bricksXPos[i] / GameConstants.TILE_SIZE;
                    lastBrokenBrickY = bricksYPos[i] / GameConstants.TILE_SIZE;
                    updateGrid();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkSolidCollision(int x, int y) {
        for (int i = 0; i < solidBricksXPos.length; i++) {
            Rect bulletRect = new Rect(x, y, x + 26, y + 26);
            Rect brickRect = new Rect(solidBricksXPos[i], solidBricksYPos[i], solidBricksXPos[i] + GameConstants.TILE_SIZE,
                    solidBricksYPos[i] + GameConstants.TILE_SIZE);
            if (bulletRect.intersect(brickRect)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkTankCollision(int x, int y) {
        for (int i = 0; i < bricksXPos.length; i++) {
            if (brickON[i] == 1) {
                Rect tankRect = new Rect(x, y, x + GameConstants.TILE_SIZE, y + GameConstants.TILE_SIZE);
                Rect brickRect = new Rect(bricksXPos[i], bricksYPos[i], bricksXPos[i] + GameConstants.TILE_SIZE, bricksYPos[i] + GameConstants.TILE_SIZE);
                if (tankRect.intersect(brickRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkTankSolidCollision(int x, int y) {
        for (int i = 0; i < solidBricksXPos.length; i++) {
            Rect tankRect = new Rect(x, y, x + GameConstants.TILE_SIZE, y + GameConstants.TILE_SIZE);
            Rect brickRect = new Rect(solidBricksXPos[i], solidBricksYPos[i],
                    solidBricksXPos[i] + GameConstants.TILE_SIZE, solidBricksYPos[i] + GameConstants.TILE_SIZE);
            if (tankRect.intersect(brickRect)) {
                return true;
            }
        }
        return false;
    }

    public void recycleBitmaps() {
        if (breakBrickImage != null && !breakBrickImage.isRecycled()) {
            breakBrickImage.recycle();
        }
        if (solidBrickImage != null && !solidBrickImage.isRecycled()) {
            solidBrickImage.recycle();
        }
    }
}
