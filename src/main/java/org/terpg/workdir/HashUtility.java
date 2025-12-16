package org.terpg.workdir;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.SplittableRandom;

public class HashUtility implements Utility {
    @Override
    public String name() {
        return "hash";
    }

    @Override
    public int energyCost() {
        return 3;
    }

    @Override
    public UtilityTag tag() {
        return UtilityTag.HASH;
    }

    @Override
    public String description() {
        return "Рассчитывает контрольную сумму HP и раскрывает часть seed";
    }

    @Override
    public UtilityResult apply(Player player, Enemy enemy, SplittableRandom rng) {
        if (player.energy() < energyCost()) {
            return UtilityResult.failure("Недостаточно энергии для hash");
        }
        player.spendEnergy(energyCost());
        String shown = enemy.representation().shownHp();
        String digest = hash(shown + enemy.representation().trueHp());
        long predicted = Math.abs(rng.nextLong()) % 1000;
        return UtilityResult.success("hash → " + digest.substring(0, 8) + "; предсказание ветки " + predicted);
    }

    private String hash(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
