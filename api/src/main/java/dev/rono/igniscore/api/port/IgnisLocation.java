package dev.rono.igniscore.api.port;

import java.util.Objects;
import java.util.UUID;

/**
 * Platform-neutral block position in a world.
 *
 * <p>Coordinates use block-center semantics (fractional values allowed).
 * Yaw and pitch default to zero when constructed without orientation.</p>
 */
public record IgnisLocation(UUID worldId, String worldName, double x, double y, double z, float yaw, float pitch) {

    /**
     * Canonical constructor; validates that {@code worldName} is non-null.
     *
     * @param worldId unique world id when known, or {@code null}
     * @param worldName human-readable world name
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param yaw horizontal rotation in degrees
     * @param pitch vertical rotation in degrees
     */
    public IgnisLocation {
        Objects.requireNonNull(worldName, "worldName");
    }

    /**
     * Creates a location without world id or orientation.
     *
     * @param worldName human-readable world name
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public IgnisLocation(String worldName, double x, double y, double z) {
        this(null, worldName, x, y, z, 0f, 0f);
    }

    /**
     * @param yaw horizontal rotation in degrees
     * @param pitch vertical rotation in degrees
     * @return a copy of this location with updated orientation
     */
    public IgnisLocation withYawPitch(float yaw, float pitch) {
        return new IgnisLocation(worldId, worldName, x, y, z, yaw, pitch);
    }

    /**
     * @param dx offset along the x axis
     * @param dy offset along the y axis
     * @param dz offset along the z axis
     * @return a copy of this location translated by the given offsets
     */
    public IgnisLocation add(double dx, double dy, double dz) {
        return new IgnisLocation(worldId, worldName, x + dx, y + dy, z + dz, yaw, pitch);
    }
}
