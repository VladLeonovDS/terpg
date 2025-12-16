package org.terpg.workdir;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameDeterminismTest {
    @Test
    void headlessRunIsDeterministic() throws IOException {
        long seed = 12345L;
        GameConfig config = new GameConfig(GameConfig.Mode.DEMO_SNAPSHOT, seed, Optional.empty(), Optional.empty());
        Game first = new Game(config);
        Game second = new Game(config);
        List<String> logA = first.runHeadless(10);
        List<String> logB = second.runHeadless(10);
        assertEquals(logA, logB, "Deterministic logs should match for same seed");
    }
}
