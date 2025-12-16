package org.terpg.workdir;

import java.util.SplittableRandom;

public class DeterminismUtility implements Utility {
    @Override
    public String name() {
        return "determinism";
    }

    @Override
    public int energyCost() {
        return 5;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.CONTROL;
    }

    @Override
    public String description() {
        return "Замораживает действие врага на 1 ход и фиксирует формат";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для determinism");
        }
        player.spendEnergy(energyCost());
        enemy.freeze(1);
        enemy.representation().setScientific(false);
        enemy.representation().setAllowFloat(false);
        return UtilityResult.success("determinism → враг заморожен на ход");
    }
}
