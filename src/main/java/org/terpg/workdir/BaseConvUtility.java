package org.terpg.workdir;

import java.util.SplittableRandom;

public class BaseConvUtility implements Utility {
    private final int targetBase;

    public BaseConvUtility(int targetBase) {
        this.targetBase = targetBase;
    }

    @Override
    public String name() {
        return "baseconv";
    }

    @Override
    public int energyCost() {
        return 6;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.BASE;
    }

    @Override
    public String description() {
        return "Сменить основание представления HP врага";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для baseconv");
        }
        player.spendEnergy(energyCost());
        enemy.representation().setBase(targetBase);
        enemy.representation().setScientific(false);
        enemy.representation().setAllowFloat(false);
        enemy.representation().setHasGlitch(false);
        long leak = Math.max(1, enemy.representation().trueHp() / 10);
        enemy.representation().damage(leak);
        return UtilityResult.success("baseconv → base " + targetBase + " (утечка " + leak + " HP)");
    }
}
