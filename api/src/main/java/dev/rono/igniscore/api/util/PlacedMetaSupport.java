package dev.rono.igniscore.api.util;

import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ephemeral metadata for placed custom blocks (placement yaw, named waypoints, etc.).
 */
public final class PlacedMetaSupport {
    private static final Map<String, Float> PLACEMENT_YAW = new ConcurrentHashMap<>();
    private static final Map<String, String> STRING_META = new ConcurrentHashMap<>();

    private PlacedMetaSupport() {
    }

    public static void recordPlacementYaw(IgnisLocation location, float yaw) {
        PLACEMENT_YAW.put(key(location), yaw);
    }

    public static float placementYaw(IgnisLocation location, float defaultYaw) {
        return PLACEMENT_YAW.getOrDefault(key(location), defaultYaw);
    }

    public static void setString(IgnisLocation location, String value) {
        STRING_META.put(key(location), value);
    }

    public static String getString(IgnisLocation location) {
        return STRING_META.get(key(location));
    }

    public static void clear(IgnisLocation location) {
        String encoded = key(location);
        PLACEMENT_YAW.remove(encoded);
        STRING_META.remove(encoded);
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
}
