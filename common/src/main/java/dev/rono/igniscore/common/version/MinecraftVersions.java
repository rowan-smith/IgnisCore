package dev.rono.igniscore.common.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses Bukkit-style version strings such as {@code 1.21.4-R0.1-SNAPSHOT}.
 */
public final class MinecraftVersions {
    private static final Pattern VERSION = Pattern.compile("(\\d+)\\.(\\d+)(?:\\.(\\d+))?");

    private MinecraftVersions() {
    }

    public static ParsedVersion parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ParsedVersion(0, 0, 0);
        }
        Matcher matcher = VERSION.matcher(raw);
        if (!matcher.find()) {
            return new ParsedVersion(0, 0, 0);
        }
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
        return new ParsedVersion(major, minor, patch);
    }

    public static boolean matchesRange(String raw, int major, int minor) {
        ParsedVersion version = parse(raw);
        return version.major() == major && version.minor() == minor;
    }

    public static boolean matchesMinorLine(String raw, int major, int minor) {
        ParsedVersion version = parse(raw);
        return version.major() == major && version.minor() == minor;
    }

    public record ParsedVersion(int major, int minor, int patch) {
        public boolean isAtLeast(int major, int minor) {
            if (this.major != major) {
                return this.major > major;
            }
            return this.minor >= minor;
        }
    }
}
