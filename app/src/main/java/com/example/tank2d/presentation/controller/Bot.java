package com.example.tank2d.presentation.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class Bot extends Tank {
    private static final String TAG = "BotDebug";
    private Tank playerTank;
    private Handler movementHandler = new Handler();
    private Handler pathFindingHandler = new Handler();
    private Handler firingHandler = new Handler();
    private List<Node> currentPath;
    private int pathIndex;
    private Brick brick;
    private ViewPlayerBot viewPlayerBot;
    private int lastTargetX;
    private int lastTargetY;
    private List<Bot> otherBots;

    public Bot(Context context, int tankX, int tankY, Bitmap tankUp, Bitmap tankDown,
               Bitmap tankLeft, Bitmap tankRight, Tank playerTank, Brick brick,
               ViewPlayerBot viewPlayerBot) {
        super(context, tankX, tankY, tankUp, tankDown, tankLeft, tankRight);
        this.playerTank = playerTank;
        this.brick = brick;
        this.viewPlayerBot = viewPlayerBot;
        this.otherBots = new ArrayList<>();
        Log.d(TAG, "Bot initialized at (" + tankX + ", " + tankY + ")");
        startMovement();
        startFiring();
        startPathFinding();
    }

    public void setOtherBots(List<Bot> bots) {
        this.otherBots = new ArrayList<>();
        for (Bot bot : bots) {
            if (bot != this) {
                this.otherBots.add(bot);
            }
        }
        Log.d(TAG, "Bot at (" + getTankX() + ", " + getTankY() + ") set to avoid " + otherBots.size() + " other bots");
    }

    private void startMovement() {
        movementHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAlive()) {
                    updateMovement();
                }
                movementHandler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void startFiring() {
        firingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAlive() && canFire()) {
                    fire();
                    Log.d(TAG, "Bot at (" + getTankX() + ", " + getTankY() + ") fired bullet");
                }
                firingHandler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void startPathFinding() {
        pathFindingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAlive()) {
                    if (shouldFindNewPath()) {
                        Log.d(TAG, "Bot at (" + getTankX() + ", " + getTankY() + ") triggered path finding");
                        currentPath = findPathToPlayer();
                        if (currentPath != null && !currentPath.isEmpty()) {
                            pathIndex = 0;
                            lastTargetX = currentPath.get(currentPath.size() - 1).x * GameConstants.TILE_SIZE;
                            lastTargetY = currentPath.get(currentPath.size() - 1).y * GameConstants.TILE_SIZE;
                            Log.d(TAG, "New path found, length=" + currentPath.size() + ", target=(" + lastTargetX + ", " + lastTargetY + ")");
                        } else {
                            Log.w(TAG, "No path found to player at (" + playerTank.getTankX() + ", " + playerTank.getTankY() + ")");
                        }
                    }
                    if (brick.hasGridChanged()) {
                        Log.d(TAG, "Grid changed detected, resetting gridChanged");
                        brick.resetGridChanged();
                    }
                }
                pathFindingHandler.postDelayed(this, 50);
            }
        }, 50);
    }

    private boolean shouldFindNewPath() {
        if (currentPath == null) {
            Log.d(TAG, "shouldFindNewPath: No current path, finding new path");
            return true;
        }
        if (pathIndex >= currentPath.size()) {
            Log.d(TAG, "shouldFindNewPath: Path ended, finding new path");
            return true;
        }
        if (brick.hasGridChanged()) {
            Log.d(TAG, "shouldFindNewPath: Grid changed (brick broken), finding new path");
            return true;
        }
        if (isCurrentPathBlocked()) {
            Log.d(TAG, "shouldFindNewPath: Current path blocked, finding new path");
            return true;
        }
        int currentTargetX = playerTank.getTankX();
        int currentTargetY = playerTank.getTankY();
        int distance = (int) Math.sqrt(Math.pow(currentTargetX - lastTargetX, 2) +
                Math.pow(currentTargetY - lastTargetY, 2));
        if (distance > 300) {
            Log.d(TAG, "shouldFindNewPath: Player moved far, distance=" + distance + ", finding new path");
            return true;
        }
        return false;
    }

    private boolean isCurrentPathBlocked() {
        if (currentPath == null || pathIndex >= currentPath.size()) {
            Log.d(TAG, "isCurrentPathBlocked: No path or path ended");
            return true;
        }
        int[][] grid = brick.getGrid();
        for (int i = pathIndex; i < currentPath.size(); i++) {
            Node node = currentPath.get(i);
            if (node.x < 0 || node.x >= GameConstants.GRID_WIDTH ||
                    node.y < 0 || node.y >= GameConstants.GRID_HEIGHT) {
                Log.w(TAG, "isCurrentPathBlocked: Invalid node at (" + node.x + ", " + node.y + ")");
                return true;
            }
            if (grid[node.x][node.y] == 1) {
                Log.w(TAG, "isCurrentPathBlocked: Path blocked at (" + node.x + ", " + node.y + ")");
                return true;
            }
        }
        Log.d(TAG, "isCurrentPathBlocked: Path is clear");
        return false;
    }

    private void updateMovement() {
        if (currentPath != null && pathIndex < currentPath.size()) {
            Node nextNode = currentPath.get(pathIndex);
            int targetX = nextNode.x * GameConstants.TILE_SIZE;
            int targetY = nextNode.y * GameConstants.TILE_SIZE;

            int deltaX = Integer.compare(targetX, getTankX()) * GameConstants.TANK_SPEED;
            int deltaY = Integer.compare(targetY, getTankY()) * GameConstants.TANK_SPEED;

            if (deltaX != 0 || deltaY != 0) {
                Log.v(TAG, "Moving bot from (" + getTankX() + ", " + getTankY() + ") to (" + targetX + ", " + targetY + ")");
                moveTank(deltaX, deltaY, viewPlayerBot);
            }

            if (Math.abs(getTankX() - targetX) < GameConstants.TANK_SPEED &&
                    Math.abs(getTankY() - targetY) < GameConstants.TANK_SPEED) {
                pathIndex++;
                Log.v(TAG, "Reached node (" + nextNode.x + ", " + nextNode.y + "), advancing to pathIndex=" + pathIndex);
            }
        } else {
            Log.v(TAG, "No valid path or path ended, stopping movement");
        }
    }

    @Override
    public void fire() {
        super.fire();
        // Đạn sẽ được di chuyển bởi ViewPlayerBot.startBulletUpdates()
    }

    private List<Node> findPathToPlayer() {
        int startX = getTankX() / GameConstants.TILE_SIZE;
        int startY = getTankY() / GameConstants.TILE_SIZE;
        int endX = playerTank.getTankX() / GameConstants.TILE_SIZE;
        int endY = playerTank.getTankY() / GameConstants.TILE_SIZE;

        Log.d(TAG, "Finding path from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ") using BFS");

        int[][] tempGrid = new int[GameConstants.GRID_WIDTH][GameConstants.GRID_HEIGHT];
        for (int i = 0; i < GameConstants.GRID_WIDTH; i++) {
            System.arraycopy(brick.getGrid()[i], 0, tempGrid[i], 0, GameConstants.GRID_HEIGHT);
        }

        int avoidedBots = 0;
        for (Bot otherBot : otherBots) {
            if (otherBot.isAlive()) {
                int otherX = otherBot.getTankX() / GameConstants.TILE_SIZE;
                int otherY = otherBot.getTankY() / GameConstants.TILE_SIZE;
                if (otherX >= 0 && otherX < GameConstants.GRID_WIDTH &&
                        otherY >= 0 && otherY < GameConstants.GRID_HEIGHT) {
                    tempGrid[otherX][otherY] = 1;
                    avoidedBots++;
                }
            }
        }
        Log.d(TAG, "Avoiding " + avoidedBots + " other bots in tempGrid");

        List<Node> path = PathFinder.findPathBFS(startX, startY, endX, endY, tempGrid);

        if (path != null) {
            StringBuilder pathStr = new StringBuilder();
            for (Node node : path) {
                pathStr.append("(").append(node.x).append(",").append(node.y).append(") ");
            }
            Log.d(TAG, "Path found (BFS), length=" + path.size() + ": " + pathStr);
        } else {
            Log.w(TAG, "No path found (BFS)");
        }
        return path;
    }

    @Override
    public void setAlive(boolean alive) {
        super.setAlive(alive);
        if (!alive) {
            Log.d(TAG, "Bot at (" + getTankX() + ", " + getTankY() + ") destroyed");
            stop();
        }
    }

    public void stop() {
        movementHandler.removeCallbacksAndMessages(null);
        pathFindingHandler.removeCallbacksAndMessages(null);
        firingHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "Bot at (" + getTankX() + ", " + getTankY() + ") stopped");
    }

    @Override
    public void recycleBitmaps() {
        super.recycleBitmaps();
        Log.d(TAG, "Bot at (" + getTankX() + ", " + getTankY() + ") recycled bitmaps");
    }
}

class Node {
    int x, y;
    Node parent;

    public Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }
}

class PathFinder {
    private static final String TAG = "PathFinderDebug";
    private static final int[] DX = {0, 0, -1, 1};
    private static final int[] DY = {-1, 1, 0, 0};

    public static List<Node> findPathBFS(int startX, int startY, int endX, int endY, int[][] grid) {
        Queue<Node> queue = new ArrayDeque<>();
        boolean[][] visited = new boolean[GameConstants.GRID_WIDTH][GameConstants.GRID_HEIGHT];

        queue.add(new Node(startX, startY, null));
        visited[startX][startY] = true;
        Log.v(TAG, "BFS started from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")");

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.x == endX && current.y == endY) {
                Log.v(TAG, "BFS found target at (" + current.x + ", " + current.y + ")");
                return constructPath(current);
            }

            for (int i = 0; i < 4; i++) {
                int nx = current.x + DX[i];
                int ny = current.y + DY[i];

                if (isValid(nx, ny, grid, visited)) {
                    visited[nx][ny] = true;
                    queue.add(new Node(nx, ny, current));
                    Log.v(TAG, "BFS exploring (" + nx + ", " + ny + ")");
                }
            }
        }
        Log.w(TAG, "BFS: No path found to (" + endX + ", " + endY + ")");
        return null;
    }

    private static boolean isValid(int x, int y, int[][] grid, boolean[][] visited) {
        boolean valid = x >= 0 && x < GameConstants.GRID_WIDTH &&
                y >= 0 && y < GameConstants.GRID_HEIGHT &&
                grid[x][y] == 0 &&
                !visited[x][y];
        if (!valid) {
            Log.v(TAG, "Invalid node (" + x + ", " + y + "): outOfBounds=" +
                    (x < 0 || x >= GameConstants.GRID_WIDTH || y < 0 || y >= GameConstants.GRID_HEIGHT) +
                    ", blocked=" + (x >= 0 && x < GameConstants.GRID_WIDTH && y >= 0 && y < GameConstants.GRID_HEIGHT && grid[x][y] == 1) +
                    ", visited=" + (x >= 0 && x < GameConstants.GRID_WIDTH && y >= 0 && y < GameConstants.GRID_HEIGHT && visited[x][y]));
        }
        return valid;
    }

    private static List<Node> constructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        Log.v(TAG, "Constructed path with " + path.size() + " nodes");
        return path;
    }
}
