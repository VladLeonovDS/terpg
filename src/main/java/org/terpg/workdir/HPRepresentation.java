package org.terpg.workdir;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class HPRepresentation {
    private long trueHp;
    private int base;
    private boolean scientific;
    private boolean allowFloat;
    private boolean hasGlitch;

    public HPRepresentation(long trueHp, int base, boolean scientific, boolean allowFloat, boolean hasGlitch) {
        this.trueHp = trueHp;
        this.base = base;
        this.scientific = scientific;
        this.allowFloat = allowFloat;
        this.hasGlitch = hasGlitch;
    }

    public long trueHp() {
        return trueHp;
    }

    public void damage(long value) {
        trueHp = Math.max(0, trueHp - value);
    }

    public void heal(long value) {
        trueHp = Math.min(Long.MAX_VALUE / 2, trueHp + value);
    }

    public int base() {
        return base;
    }

    public boolean isScientific() {
        return scientific;
    }

    public boolean isAllowFloat() {
        return allowFloat;
    }

    public boolean hasGlitch() {
        return hasGlitch;
    }

    public void setBase(int base) {
        if (base < 2 || base > 36) {
            throw new IllegalArgumentException("base must be 2..36");
        }
        this.base = base;
    }

    public void setScientific(boolean scientific) {
        this.scientific = scientific;
    }

    public void setAllowFloat(boolean allowFloat) {
        this.allowFloat = allowFloat;
    }

    public void setHasGlitch(boolean hasGlitch) {
        this.hasGlitch = hasGlitch;
    }

    public String shownHp() {
        if (hasGlitch) {
            return "<garbage>";
        }
        if (scientific) {
            BigDecimal decimal = BigDecimal.valueOf(trueHp);
            return decimal.toString().toLowerCase(Locale.ROOT) + "e0";
        }
        if (allowFloat) {
            double floatValue = trueHp + 0.25;
            return String.format(Locale.ROOT, "%f", floatValue);
        }
        return Long.toString(trueHp, base).toUpperCase(Locale.ROOT);
    }

    public HPRepresentation convertedToBase(int newBase) {
        HPRepresentation copy = copy();
        copy.setBase(newBase);
        copy.setScientific(false);
        copy.setAllowFloat(false);
        copy.setHasGlitch(false);
        return copy;
    }

    public HPRepresentation sanitize() {
        HPRepresentation copy = copy();
        copy.setHasGlitch(false);
        if (copy.trueHp < 0) {
            copy.trueHp = Math.abs(copy.trueHp);
        }
        copy.setAllowFloat(false);
        return copy;
    }

    public Optional<Long> parseShownToTrue(String value) {
        try {
            if (value == null) return Optional.empty();
            String trimmed = value.replace("_", "").trim();
            if (trimmed.equalsIgnoreCase("nan") || trimmed.equals("∞")) {
                return Optional.empty();
            }
            if (scientific) {
                return Optional.of((long) Double.parseDouble(trimmed));
            }
            if (allowFloat) {
                return Optional.of((long) Double.parseDouble(trimmed));
            }
            return Optional.of(new BigInteger(trimmed, base).longValue());
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public HPRepresentation copy() {
        return new HPRepresentation(trueHp, base, scientific, allowFloat, hasGlitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HPRepresentation that = (HPRepresentation) o;
        return trueHp == that.trueHp && base == that.base && scientific == that.scientific && allowFloat == that.allowFloat && hasGlitch == that.hasGlitch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(trueHp, base, scientific, allowFloat, hasGlitch);
    }
}
