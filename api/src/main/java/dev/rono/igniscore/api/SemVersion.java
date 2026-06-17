package dev.rono.igniscore.api;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal semver parser used for Ignis extension {@code api-version} checks.
 *
 * <p>Supports {@code major.minor.patch} with an optional {@code -preRelease} suffix. Pre-release
 * versions are treated as older than the corresponding release when comparing compatibility.</p>
 *
 * @param major non-negative major version component
 * @param minor non-negative minor version component
 * @param patch non-negative patch version component
 * @param preRelease optional pre-release label (for example {@code rc1}), or {@code null}
 * @see IgnisApiVersion
 */
public record SemVersion(int major, int minor, int patch, String preRelease) implements Comparable<SemVersion> {
    private static final Pattern PATTERN = Pattern.compile(
            "^(\\d+)\\.(\\d+)\\.(\\d+)(?:-([0-9A-Za-z.-]+))?$");

    /**
     * Compact constructor validating non-negative version components.
     */
    public SemVersion {
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version components must be non-negative: " + major + "." + minor + "." + patch);
        }
    }

    /**
     * Parses a semver string into a {@link SemVersion}.
     *
     * @param raw version text in {@code major.minor.patch} or {@code major.minor.patch-pre} form
     * @return parsed version
     * @throws IllegalArgumentException when the input is blank or does not match the supported pattern
     */
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
     * Returns {@code true} when {@code runtime} is the same as or newer than {@code required} on the same major line.
     *
     * <p>Pre-release versions are treated as older than the corresponding release. When both sides
     * are pre-releases at the same release triple, labels must match exactly.</p>
     *
     * @param runtime API version provided by the running core
     * @param required API version declared by an extension
     * @return {@code true} when the runtime can satisfy the extension requirement
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

    /**
     * Returns whether this version carries a non-blank pre-release suffix.
     *
     * @return {@code true} when {@link #preRelease} is present
     */
    public boolean isPreRelease() {
        return preRelease != null && !preRelease.isBlank();
    }

    /**
     * Compares this version to another using semver ordering.
     *
     * <p>Release triples are compared first; pre-release labels sort before their release
     * counterpart and are compared lexicographically when both sides are pre-releases.</p>
     *
     * @param other version to compare against
     * @return negative, zero, or positive as this version is older, equal, or newer than {@code other}
     */
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

    /**
     * Returns the canonical string form {@code major.minor.patch} or {@code major.minor.patch-preRelease}.
     *
     * @return normalized semver text
     */
    @Override
    public String toString() {
        String base = major + "." + minor + "." + patch;
        return isPreRelease() ? base + "-" + preRelease : base;
    }
}
