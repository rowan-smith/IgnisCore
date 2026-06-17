package dev.rono.igniscore.api.extension;

import java.util.Locale;

/**
 * High-level extension behavior profiles declared in manifests via {@code profiles}.
 *
 * <p>Profiles are documentation and validation hints — they do not change runtime
 * dispatch by themselves. Use them to describe which strategy callbacks and services
 * an extension expects to use.</p>
 *
 * @see ExtensionManifest#getProfiles()
 */
public enum ExtensionProfile {
    /** Fuse-based explosive with active {@code onTick} / {@code onTrigger} lifecycle. */
    FUSE("fuse"),

    /** Passive block that registers placed callbacks and optional repeating ticks. */
    PLACED("placed"),

    /** Right-click open/interact block; may combine with {@link #PLACED_HOOKS}. */
    INTERACT("interact"),

    /** Interact block that also needs {@code onPlaced} / {@code onPlacedBreak} registration. */
    PLACED_HOOKS("placed-hooks"),

    /** Item used from air or on blocks via {@code onItemUse}. */
    ITEM_USE("item-use"),

    /** Chest-style or processing GUI backed by {@code ExtensionSupport} inventories. */
    PROCESSING_GUI("processing-gui"),

    /** Collects drops from block breaks or item spawns near the block. */
    DROP_COLLECTOR("drop-collector");

    private final String manifestKey;

    ExtensionProfile(String manifestKey) {
        this.manifestKey = manifestKey;
    }

    /**
     * @return YAML manifest token for this profile
     */
    public String manifestKey() {
        return manifestKey;
    }

    /**
     * Parses a manifest entry (case-insensitive, hyphen or underscore).
     *
     * @throws IllegalArgumentException if the token is unknown
     */
    public static ExtensionProfile fromManifest(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Profile token must not be blank");
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        for (ExtensionProfile profile : values()) {
            if (profile.manifestKey.equals(normalized)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Unknown profiles entry: " + raw);
    }
}
