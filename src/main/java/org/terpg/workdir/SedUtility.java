package org.terpg.workdir;

import java.util.SplittableRandom;

public class SedUtility implements Utility {
    private final String search;
    private final String replace;

    public SedUtility(String search, String replace) {
        this.search = search;
        this.replace = replace;
    }

    @Override
    public String name() {
        return "sed";
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
        return "Точечная замена символов HP";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для sed");
        }
        player.spendEnergy(energyCost());
        String shown = enemy.representation().shownHp();
        String mutated = shown.replace(search, replace);
        long diff = shown.equals(mutated) ? 0 : rng.nextLong(1, 5);
        enemy.representation().damage(diff);
        return UtilityResult.success("sed s/" + search + "/" + replace + "/ → урон " + diff);
    }
}
