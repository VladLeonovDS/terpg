package org.terpg.workdir;

import java.util.SplittableRandom;

public class FormatUtility implements Utility {
    @Override
    public String name() {
        return "fmt";
    }

    @Override
    public int energyCost() {
        return 4;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.FORMAT;
    }

    @Override
    public String description() {
        return "Убирает разделители и странные суффиксы у HP";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для fmt");
        }
        player.spendEnergy(energyCost());
        HPRepresentation representation = enemy.representation();
        representation.setScientific(false);
        representation.setHasGlitch(false);
        long bonus = rng.nextLong(1, 4);
        representation.damage(bonus);
        return UtilityResult.success("fmt → нормализован формат, потеря " + bonus + " HP");
    }
}
