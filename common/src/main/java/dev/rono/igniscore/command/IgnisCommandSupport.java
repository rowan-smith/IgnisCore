package dev.rono.igniscore.command;

public final class IgnisCommandSupport {
    private IgnisCommandSupport() {
    }

    public static String[] splitArgs(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[0];
        }
        return raw.trim().split("\\s+");
    }
}
