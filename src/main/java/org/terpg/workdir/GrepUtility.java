package org.terpg.workdir;

import java.util.SplittableRandom;

public class GrepUtility implements Utility {
    private final String pattern;

    public GrepUtility(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String name() {
        return "grep";
    }

    @Override
    public int energyCost() {
        return 4;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.INSPECT;
    }

    @Override
    public String description() {
        return "Ищет паттерн в HP и наносит структурный урон";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для grep");
        }
        player.spendEnergy(energyCost());
        String shown = enemy.representation().shownHp();
        int occurrences = shown.split(pattern, -1).length - 1;
        long damage = Math.max(1, occurrences * 2L);
        enemy.representation().damage(damage);
        return UtilityResult.success("grep /" + pattern + "/ → найдено " + occurrences + ", урон " + damage);
    }
}
