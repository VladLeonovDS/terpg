package org.terpg.workdir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.SplittableRandom;

public class LevelGenerator {
    private final SplittableRandom rng;

    public LevelGenerator(SplittableRandom rng) {
        this.rng = rng;
    }

    public Level demoLevel() {
        Level level = new Level(20, 10);
        level.setPlayerStart(new Position(1, 1));
        for (int x = 0; x < level.width(); x++) {
            level.setTile(new Position(x, 0), '#');
            level.setTile(new Position(x, level.height() - 1), '#');
        }
        for (int y = 0; y < level.height(); y++) {
            level.setTile(new Position(0, y), '#');
            level.setTile(new Position(level.width() - 1, y), '#');
        }
        placeEnemy(level, new Position(5, 4), EnemyType.JSON);
        placeEnemy(level, new Position(10, 6), EnemyType.LOG);
        placeEnemy(level, new Position(15, 3), EnemyType.ARCHIVE);
        return level;
    }

    public Level sandboxLevel(int width, int height) {
        Level level = new Level(width, height);
        level.setPlayerStart(new Position(width / 2, height / 2));
        int enemies = rng.nextInt(3, 7);
        for (int i = 0; i < enemies; i++) {
            Position pos = randomEmpty(level);
            placeEnemy(level, pos, EnemyType.values()[rng.nextInt(EnemyType.values().length)]);
        }
        return level;
    }

    public Level fromReadOnlyScan(Path root) throws IOException {
        List<Path> nodes = Files.list(root).limit(10).toList();
        Level level = new Level(20, Math.max(10, nodes.size() + 4));
        level.setPlayerStart(new Position(1, 1));
        int y = 2;
        for (Path node : nodes) {
            EnemyType type = node.toFile().isDirectory() ? EnemyType.DIR : EnemyType.TXT;
            placeEnemy(level, new Position(2 + rng.nextInt(0, level.width() - 3), y), type);
            y += 1;
        }
        return level;
    }

    private void placeEnemy(Level level, Position position, EnemyType type) {
        long hp = rng.nextLong(20, 80);
        int base = rng.nextInt(2, 16);
        HPRepresentation representation = new HPRepresentation(hp, base, rng.nextBoolean(), rng.nextBoolean(), rng.nextInt(0, 4) == 0);
        Enemy enemy = new Enemy(type, hp, representation);
        level.addEnemy(position, enemy);
    }

    private Position randomEmpty(Level level) {
        while (true) {
            Position pos = new Position(rng.nextInt(1, level.width() - 1), rng.nextInt(1, level.height() - 1));
            if (!level.enemies().containsKey(pos)) {
                return pos;
            }
        }
    }
}
