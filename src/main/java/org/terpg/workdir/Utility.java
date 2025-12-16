package org.terpg.workdir;

import java.util.SplittableRandom;

public interface Utility {
    String name();

    int energyCost();

    UtilityTag tag();

    String description();

    UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng);
}
