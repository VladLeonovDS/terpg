package org.terpg.workdir;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SnapshotLoader {
    private final Gson gson = new Gson();

    public Level loadDemoSnapshot() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/snapshots/demo.json"))) {
            SnapshotDefinition definition = gson.fromJson(reader, SnapshotDefinition.class);
            return toLevel(definition);
        }
    }

    public Level loadFromFile(Path path) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
            SnapshotDefinition definition = gson.fromJson(reader, SnapshotDefinition.class);
            return toLevel(definition);
        }
    }

    private Level toLevel(SnapshotDefinition def) {
        Level level = new Level(def.width, def.height);
        level.setPlayerStart(new Position(def.playerStart.x, def.playerStart.y));
        for (SnapshotDefinition.Wall wall : def.walls) {
            level.setTile(new Position(wall.x, wall.y), '#');
        }
        for (SnapshotDefinition.EnemySpawn spawn : def.enemies) {
            HPRepresentation hp = new HPRepresentation(spawn.hp, spawn.base, spawn.scientific, spawn.allowFloat, spawn.glitch);
            level.addEnemy(new Position(spawn.x, spawn.y), new Enemy(spawn.type, spawn.hp, hp));
        }
        return level;
    }

    static class SnapshotDefinition {
        int width;
        int height;
        PlayerStart playerStart;
        List<Wall> walls;
        List<EnemySpawn> enemies;

        static class PlayerStart {
            int x;
            int y;
        }

        static class Wall {
            int x;
            int y;
        }

        static class EnemySpawn {
            int x;
            int y;
            EnemyType type;
            long hp;
            int base;
            boolean scientific;
            boolean allowFloat;
            boolean glitch;
        }
    }
}
