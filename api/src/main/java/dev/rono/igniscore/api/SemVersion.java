package dev.rono.igniscore.api;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal semver parser used for Ignis extension {@code api-version} checks.
 * Supports {@code major.minor.patch} with an optional {@code -preRelease} suffix.
 */
public record SemVersion(int major, int minor, int patch, String preRelease) implements Comparable<SemVersion> {
    private static final Pattern PATTERN = Pattern.compile(
            "^(\\d+)\\.(\\d+)\\.(\\d+)(?:-([0-9A-Za-z.-]+))?$");

    public SemVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version components must be non-negative: " + major + "." + minor + "." + patch);
        }
    }

    public static SemVersion parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Version must not be blank");
        }
        Matcher matcher = PATTERN.matcher(raw.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid semver: " + raw);
        }
        return new SemVersion(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)),
                matcher.group(4));
    }

    /**
     * Returns true when {@code runtime} is the same as or newer than {@code required} on the same major line.
     * Pre-release versions are treated as older than the corresponding release.
     */
    public static boolean isRuntimeCompatibleWith(SemVersion runtime, SemVersion required) {
        Objects.requireNonNull(runtime, "runtime");
        Objects.requireNonNull(required, "required");
        if (runtime.major != required.major) {
            return false;
        }
        int baseCompare = compareRelease(runtime, required);
        if (baseCompare > 0) {
            return true;
        }
        if (baseCompare < 0) {
            return false;
        }
        return !runtime.isPreRelease() || !required.isPreRelease() || runtime.preRelease.equals(required.preRelease);
    }

    public boolean isPreRelease() {
        return preRelease != null && !preRelease.isBlank();
    }

    @Override
    public int compareTo(SemVersion other) {
        int releaseCompare = compareRelease(this, other);
        if (releaseCompare != 0) {
            return releaseCompare;
        }
        if (!isPreRelease() && other.isPreRelease()) {
            return 1;
        }
        if (isPreRelease() && !other.isPreRelease()) {
            return -1;
        }
        if (isPreRelease() && other.isPreRelease()) {
            return preRelease.compareTo(other.preRelease);
        }
        return 0;
    }

    private static int compareRelease(SemVersion left, SemVersion right) {
        if (left.major != right.major) {
            return Integer.compare(left.major, right.major);
        }
        if (left.minor != right.minor) {
            return Integer.compare(left.minor, right.minor);
        }
        return Integer.compare(left.patch, right.patch);
    }

    @Override
    public String toString() {
        String base = major + "." + minor + "." + patch;
        return isPreRelease() ? base + "-" + preRelease : base;
    }
}
