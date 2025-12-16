package org.terpg.workdir;

import java.nio.file.Path;
import java.util.Optional;

public class GameConfig {
    public enum Mode {
        SANDBOX,
        READ_ONLY_SCAN,
        DEMO_SNAPSHOT
    }

    private final Mode mode;
    private final long seed;
    private final Optional<Path> snapshot;
    private final Optional<Path> replay;

    public GameConfig(Mode mode, long seed, Optional<Path> snapshot, Optional<Path> replay) {
        this.mode = mode;
        this.seed = seed;
        this.snapshot = snapshot;
        this.replay = replay;
    }

    public Mode mode() {
        return mode;
    }

    public long seed() {
        return seed;
    }

    public Optional<Path> snapshot() {
        return snapshot;
    }

    public Optional<Path> replay() {
        return replay;
    }
}
