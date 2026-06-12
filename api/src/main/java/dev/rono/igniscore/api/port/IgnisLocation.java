package dev.rono.igniscore.api.port;

import java.util.Objects;
import java.util.UUID;

/**
 * Platform-neutral block position in a world.
 */
public record IgnisLocation(UUID worldId, String worldName, double x, double y, double z, float yaw, float pitch) {

    public IgnisLocation {
        Objects.requireNonNull(worldName, "worldName");
    }

    public IgnisLocation(String worldName, double x, double y, double z) {
        this(null, worldName, x, y, z, 0f, 0f);
    }

    public IgnisLocation withYawPitch(float yaw, float pitch) {
        return new IgnisLocation(worldId, worldName, x, y, z, yaw, pitch);
    }

    public IgnisLocation add(double dx, double dy, double dz) {
        return new IgnisLocation(worldId, worldName, x + dx, y + dy, z + dz, yaw, pitch);
    }
}
