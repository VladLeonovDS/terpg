package org.terpg.workdir;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

public class BattleEngine {
    private final SplittableRandom rng;

    public BattleEngine(SplittableRandom rng) {
        this.rng = rng;
    }

    public List<String> fight(Player player, Enemy enemy) {
        List<String> log = new ArrayList<>();
        log.add("В бой вступает " + enemy.type() + " с HP " + enemy.representation().shownHp());
        int turn = 0;
        while (!enemy.isDefeated() && player.hp() > 0 && turn < 30) {
            turn++;
            log.addAll(playerTurn(player, enemy));
            if (enemy.isDefeated()) {
                log.add("Враг повержен!");
                break;
            }
            log.addAll(enemyTurn(player, enemy));
        }
        if (player.hp() <= 0) {
            log.add("Игрок пал в бою");
        }
        return log;
    }

    private List<String> playerTurn(Player player, Enemy enemy) {
        List<String> lines = new ArrayList<>();
        player.restoreEnergy(2);
        Utility utility = chooseUtility(player);
        if (utility != null) {
            lines.add(utility.apply(player, enemy, rng.split()).logLine());
        }
        long baseDamage = Math.max(1, 4 - enemy.representation().base() / 10);
        enemy.representation().damage(baseDamage);
        lines.add("Базовая атака нанесла " + baseDamage + " HP");
        return lines;
    }

    private Utility chooseUtility(Player player) {
        return player.utilities().stream()
                .filter(u -> player.energy() >= u.energyCost())
                .findFirst()
                .orElse(null);
    }

    private List<String> enemyTurn(Player player, Enemy enemy) {
        List<String> lines = new ArrayList<>();
        if (enemy.frozenTurns() > 0) {
            lines.add("Враг заморожен и пропускает ход");
            enemy.tickFrozen();
            return lines;
        }
        long damage = rng.nextLong(2, 6);
        boolean formatAttack = rng.nextBoolean();
        if (formatAttack) {
            enemy.representation().setBase(Math.max(2, enemy.representation().base() - 1));
            lines.add("Враг меняет своё основание на " + enemy.representation().base());
        } else {
            player.damage(damage);
            lines.add("Форматная атака по игроку на " + damage + " HP");
        }
        return lines;
    }
}
