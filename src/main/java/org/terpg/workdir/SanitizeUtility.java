package org.terpg.workdir;

import java.util.SplittableRandom;

public class SanitizeUtility implements Utility {
    @Override
    public String name() {
        return "sanitize";
    }

    @Override
    public int energyCost() {
        return 5;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.SANITIZE;
    }

    @Override
    public String description() {
        return "Лечит NaN/∞/битый формат и раскрывает реальный HP";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для sanitize");
        }
        player.spendEnergy(energyCost());
        HPRepresentation sanitized = enemy.representation().sanitize();
        long restored = enemy.representation().trueHp() - sanitized.trueHp();
        enemy.representation().setHasGlitch(false);
        enemy.representation().setAllowFloat(false);
        enemy.representation().setScientific(false);
        enemy.representation().damage(0);
        return UtilityResult.success("sanitize → формат очищен" + (restored != 0 ? " (коррекция " + restored + ")" : ""));
    }
}
