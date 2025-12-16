package org.terpg.workdir;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Level {
    private final int width;
    private final int height;
    private final char[][] cells;
    private final Map<Position, Enemy> enemies = new HashMap<>();
    private Position playerStart;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = '.';
            }
        }
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public char[][] cells() {
        return cells;
    }

    public void setTile(Position pos, char c) {
        cells[pos.y()][pos.x()] = c;
    }

    public char tile(Position pos) {
        return cells[pos.y()][pos.x()];
    }

    public boolean inBounds(Position pos) {
        return pos.x() >= 0 && pos.x() < width && pos.y() >= 0 && pos.y() < height;
    }

    public Map<Position, Enemy> enemies() {
        return enemies;
    }

    public void addEnemy(Position pos, Enemy enemy) {
        enemies.put(pos, enemy);
        setTile(pos, 'E');
    }

    public Optional<Enemy> enemyAt(Position pos) {
        return Optional.ofNullable(enemies.get(pos));
    }

    public void setPlayerStart(Position pos) {
        this.playerStart = pos;
    }

    public Position playerStart() {
        return playerStart;
    }
}
