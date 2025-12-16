package org.terpg.workdir;

import java.util.SplittableRandom;

public class EntropyUtility implements Utility {
    @Override
    public String name() {
        return "entropy";
    }

    @Override
    public int energyCost() {
        return 7;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.ENTROPY;
    }

    @Override
    public String description() {
        return "Вносит хаос в представление HP (может усилить врага)";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для entropy");
        }
        player.spendEnergy(energyCost());
        boolean buff = rng.nextBoolean();
        if (buff) {
            enemy.representation().heal(5);
        } else {
            enemy.representation().damage(5);
        }
        enemy.representation().setAllowFloat(rng.nextBoolean());
        enemy.representation().setScientific(rng.nextBoolean());
        enemy.representation().setHasGlitch(rng.nextInt(0, 3) == 0);
        int newBase = rng.nextInt(2, 16);
        enemy.representation().setBase(newBase);
        return UtilityResult.success("entropy → base=" + newBase + (buff ? ", враг усилился" : ", враг ослаб"));
    }
}
