package org.terpg.workdir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.SplittableRandom;

public class Game {
    private final GameConfig config;
    private final SplittableRandom rng;
    private Level level;
    private Position playerPos;
    private final Player player = new Player(120, 20, 15);
    private final List<String> eventLog = new ArrayList<>();

    public Game(GameConfig config) {
        this.config = config;
        this.rng = new SplittableRandom(config.seed());
    }

    public void run() throws IOException {
        loadLevel();
        seedUtilities();
        render();
        try (Scanner scanner = new Scanner(System.in)) {
            while (player.hp() > 0) {
                System.out.print("Ход (WASD, u — утилита, q — выход): ");
                String input = scanner.nextLine();
                if (input.isBlank()) continue;
                char command = Character.toLowerCase(input.charAt(0));
                if (command == 'q') {
                    break;
                }
                if (command == 'u') {
                    useUtility(scanner);
                } else {
                    move(command);
                }
                render();
            }
        }
    }

    public List<String> runHeadless(int turns) throws IOException {
        loadLevel();
        seedUtilities();
        for (int i = 0; i < turns && player.hp() > 0; i++) {
            autoStep();
        }
        return List.copyOf(eventLog);
    }

    private void seedUtilities() {
        player.addUtility(new BaseConvUtility(10));
        player.addUtility(new FormatUtility());
        player.addUtility(new SanitizeUtility());
        player.addUtility(new HashUtility());
        player.addUtility(new GrepUtility("0"));
        player.addUtility(new SedUtility("e", "+"));
        player.addUtility(new DeterminismUtility());
        player.addUtility(new EntropyUtility());
    }

    private void useUtility(Scanner scanner) {
        System.out.println("Выберите утилиту:");
        for (int i = 0; i < player.utilities().size(); i++) {
            Utility utility = player.utilities().get(i);
            System.out.printf("%d) %s [%s] — %s (энергия %d)\n", i + 1, utility.name(), utility.tag(), utility.description(), utility.energyCost());
        }
        System.out.print("> ");
        if (!scanner.hasNextLine()) return;
        String choice = scanner.nextLine();
        try {
            int idx = Integer.parseInt(choice) - 1;
            if (idx >= 0 && idx < player.utilities().size()) {
                Optional<Enemy> enemy = level.enemyAt(playerPos);
                if (enemy.isEmpty()) {
                    System.out.println("Рядом нет врага");
                    return;
                }
                UtilityResult result = player.utilities().get(idx).apply(player, enemy.get(), rng.split());
                eventLog.add(result.logLine());
                System.out.println(result.logLine());
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private void move(char command) {
        Position delta = switch (command) {
            case 'w' -> new Position(0, -1);
            case 's' -> new Position(0, 1);
            case 'a' -> new Position(-1, 0);
            case 'd' -> new Position(1, 0);
            default -> new Position(0, 0);
        };
        Position next = playerPos.move(delta.x(), delta.y());
        if (!level.inBounds(next) || level.tile(next) == '#') {
            eventLog.add("Столкновение со стеной");
            return;
        }
        playerPos = next;
        eventLog.add("Игрок переместился в " + next);
        level.enemyAt(playerPos).ifPresent(this::engageEnemy);
    }

    private void autoStep() {
        move("wasd".charAt(rng.nextInt(0, 4)));
    }

    private void engageEnemy(Enemy enemy) {
        BattleEngine engine = new BattleEngine(rng.split());
        List<String> battleLog = engine.fight(player, enemy);
        eventLog.addAll(battleLog);
        level.enemies().remove(playerPos);
        level.setTile(playerPos, '.');
    }

    private void render() {
        char[][] cells = level.cells();
        for (int y = 0; y < level.height(); y++) {
            for (int x = 0; x < level.width(); x++) {
                if (playerPos.x() == x && playerPos.y() == y) {
                    System.out.print('@');
                } else {
                    System.out.print(cells[y][x]);
                }
            }
            System.out.println();
        }
        System.out.printf("HP:%d Shield:%d Energy:%d  | Лог: %s\n", player.hp(), player.shield(), player.energy(),
                eventLog.isEmpty() ? "—" : eventLog.get(eventLog.size() - 1));
    }

    private void loadLevel() throws IOException {
        LevelGenerator generator = new LevelGenerator(rng.split());
        switch (config.mode()) {
            case DEMO_SNAPSHOT -> {
                SnapshotLoader loader = new SnapshotLoader();
                if (config.snapshot().isPresent()) {
                    level = loader.loadFromFile(config.snapshot().get());
                } else {
                    level = loader.loadDemoSnapshot();
                }
            }
            case READ_ONLY_SCAN -> {
                Path root = config.snapshot().orElse(Path.of("."));
                level = generator.fromReadOnlyScan(root);
            }
            default -> level = generator.sandboxLevel(30, 15);
        }
        playerPos = level.playerStart();
    }
}
