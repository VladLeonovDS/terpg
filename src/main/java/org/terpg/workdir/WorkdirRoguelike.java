package org.terpg.workdir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class WorkdirRoguelike {
    public static void main(String[] args) throws IOException {
        CliArgs parsed = CliArgs.parse(args);
        GameConfig config = new GameConfig(parsed.mode, parsed.seed, parsed.snapshot, parsed.replay);
        LanternaArenaGame arenaGame = new LanternaArenaGame(config);
        arenaGame.run();
    }

    static class CliArgs {
        final GameConfig.Mode mode;
        final long seed;
        final Optional<Path> snapshot;
        final Optional<Path> replay;

        CliArgs(GameConfig.Mode mode, long seed, Optional<Path> snapshot, Optional<Path> replay) {
            this.mode = mode;
            this.seed = seed;
            this.snapshot = snapshot;
            this.replay = replay;
        }

        static CliArgs parse(String[] args) {
            GameConfig.Mode mode = GameConfig.Mode.SANDBOX;
            long seed = System.currentTimeMillis();
            Optional<Path> snapshot = Optional.empty();
            Optional<Path> replay = Optional.empty();
            for (String arg : args) {
                if (arg.startsWith("--seed=")) {
                    seed = Long.parseLong(arg.substring("--seed=".length()));
                } else if (arg.startsWith("--snapshot=")) {
                    snapshot = Optional.of(Path.of(arg.substring("--snapshot=".length())));
                    mode = GameConfig.Mode.DEMO_SNAPSHOT;
                } else if (arg.startsWith("--mode=")) {
                    String m = arg.substring("--mode=".length());
                    mode = switch (m) {
                        case "sandbox" -> GameConfig.Mode.SANDBOX;
                        case "readonlyscan" -> GameConfig.Mode.READ_ONLY_SCAN;
                        case "demo" -> GameConfig.Mode.DEMO_SNAPSHOT;
                        default -> mode;
                    };
                } else if (arg.startsWith("--replay=")) {
                    replay = Optional.of(Path.of(arg.substring("--replay=".length())));
                }
            }
            return new CliArgs(mode, seed, snapshot, replay);
        }
    }
}
