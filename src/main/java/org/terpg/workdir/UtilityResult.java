package org.terpg.workdir;

public record UtilityResult(boolean success, String logLine) {
    public static UtilityResult success(String logLine) {
        return new UtilityResult(true, logLine);
    }

    public static UtilityResult failure(String logLine) {
        return new UtilityResult(false, logLine);
    }
}
