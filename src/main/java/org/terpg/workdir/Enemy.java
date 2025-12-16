package org.terpg.workdir;

import java.util.ArrayList;
import java.util.List;

public class Enemy {
    private final EnemyType type;
    private final long maxHp;
    private final List<String> weaknesses = new ArrayList<>();
    private final HPRepresentation representation;
    private int frozenTurns = 0;

    public Enemy(EnemyType type, long maxHp, HPRepresentation representation) {
        this.type = type;
        this.maxHp = maxHp;
        this.representation = representation;
    }

    public EnemyType type() {
        return type;
    }

    public long maxHp() {
        return maxHp;
    }

    public HPRepresentation representation() {
        return representation;
    }

    public List<String> weaknesses() {
        return weaknesses;
    }

    public boolean isDefeated() {
        return representation.trueHp() <= 0;
    }

    public int frozenTurns() {
        return frozenTurns;
    }

    public void freeze(int turns) {
        frozenTurns = Math.max(frozenTurns, turns);
    }

    public void tickFrozen() {
        if (frozenTurns > 0) {
            frozenTurns -= 1;
        }
    }
}
