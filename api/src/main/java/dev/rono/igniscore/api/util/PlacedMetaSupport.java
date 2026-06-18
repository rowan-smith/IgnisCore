package dev.rono.igniscore.api.util;

import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Process-wide ephemeral metadata keyed by placed block location.
 *
 * <p>Unlike {@link dev.rono.igniscore.api.model.RuntimeBlockInstance#getData() persistent NBT},
 * values here live only in memory and are cleared when the block is removed. Use for placement
 * yaw, waypoint labels, tripwire pairs, or other short-lived state that does not need to survive
 * restarts.</p>
 *
 * <p>Keys are derived from world id or name plus floored block coordinates via
 * {@link Locations#toBlock(IgnisLocation)}.</p>
 */
public final class PlacedMetaSupport {
    private static final Map<String, Float> PLACEMENT_YAW = new ConcurrentHashMap<>();
    private static final Map<String, String> STRING_META = new ConcurrentHashMap<>();
    private static final Map<String, String> TRIPWIRE_PARTNERS = new ConcurrentHashMap<>();

    private PlacedMetaSupport() {
    }

    /**
     * Records the yaw used when a custom block was placed at {@code location}.
     *
     * @param location placed block position
     * @param yaw placement yaw in degrees
     */
    public static void recordPlacementYaw(IgnisLocation location, float yaw) {
        PLACEMENT_YAW.put(key(location), yaw);
    }

    /**
     * Returns the recorded placement yaw, or {@code defaultYaw} when none was stored.
     *
     * @param location block position to query
     * @param defaultYaw value used when no placement yaw was recorded
     * @return stored yaw or the default
     */
    public static float placementYaw(IgnisLocation location, float defaultYaw) {
        return PLACEMENT_YAW.getOrDefault(key(location), defaultYaw);
    }

    /**
     * Stores a string label or tag for the block at {@code location}.
     *
     * @param location block position
     * @param value metadata string (for example a waypoint name)
     */
    public static void setString(IgnisLocation location, String value) {
        STRING_META.put(key(location), value);
    }

    /**
     * Returns the string metadata at {@code location}, or {@code null} when unset.
     *
     * @param location block position to query
     * @return stored string or {@code null}
     */
    public static String getString(IgnisLocation location) {
        return STRING_META.get(key(location));
    }

    /**
     * Links two tripwire charge locations as paired triggers.
     */
    public static void linkTripwire(IgnisLocation first, IgnisLocation second) {
        String a = key(first);
        String b = key(second);
        TRIPWIRE_PARTNERS.put(a, b);
        TRIPWIRE_PARTNERS.put(b, a);
    }

    /**
     * Returns the paired tripwire location, or {@code null} when none is linked.
     */
    public static IgnisLocation tripwirePartner(IgnisLocation location) {
        String partner = TRIPWIRE_PARTNERS.get(key(location));
        if (partner == null) {
            return null;
        }
        return decode(partner);
    }

    /**
     * Removes all ephemeral metadata for the block at {@code location}.
     *
     * @param location block position to clear
     */
    public static void clear(IgnisLocation location) {
        String encoded = key(location);
        PLACEMENT_YAW.remove(encoded);
        STRING_META.remove(encoded);
        String partnerKey = TRIPWIRE_PARTNERS.remove(encoded);
        if (partnerKey != null) {
            TRIPWIRE_PARTNERS.remove(partnerKey);
        }
    }

    private static String key(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        UUID worldId = block.worldId();
        String worldName = block.worldName() == null ? "world" : block.worldName();
        UUID resolved = worldId != null ? worldId : UUID.nameUUIDFromBytes(worldName.getBytes());
        return resolved + ":" + worldName + ":"
                + (int) Math.floor(block.x()) + ":"
                + (int) Math.floor(block.y()) + ":"
                + (int) Math.floor(block.z());
    }

    private static IgnisLocation decode(String encoded) {
        String[] parts = encoded.split(":");
        if (parts.length != 5) {
            return null;
        }
        try {
            return new IgnisLocation(
                    UUID.fromString(parts[0]),
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    0f,
                    0f);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
