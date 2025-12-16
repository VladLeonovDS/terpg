package org.terpg.workdir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private long hp;
    private long shield;
    private long energy;
    private final List<Utility> utilities = new ArrayList<>();

    public Player(long hp, long shield, long energy) {
        this.hp = hp;
        this.shield = shield;
        this.energy = energy;
    }

    public long hp() {
        return hp;
    }

    public long shield() {
        return shield;
    }

    public long energy() {
        return energy;
    }

    public void damage(long value) {
        long remaining = value;
        if (shield > 0) {
            long absorbed = Math.min(shield, remaining);
            shield -= absorbed;
            remaining -= absorbed;
        }
        hp = Math.max(0, hp - remaining);
    }

    public void heal(long value) {
        hp = Math.min(hp + value, 200);
    }

    public void restoreEnergy(long value) {
        energy = Math.min(energy + value, 100);
    }

    public void spendEnergy(long value) {
        energy = Math.max(0, energy - value);
    }

    public List<Utility> utilities() {
        return Collections.unmodifiableList(utilities);
    }

    public void addUtility(Utility utility) {
        utilities.add(utility);
    }
}
