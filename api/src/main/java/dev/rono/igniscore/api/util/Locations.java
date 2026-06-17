package dev.rono.igniscore.api.util;

import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Normalizes {@link IgnisLocation} coordinates for block-aligned math and display placement.
 *
 * <p>Strategies and the core runtime use these helpers to compare block keys consistently
 * regardless of sub-block fractional positions.</p>
 */
public final class Locations {

    private Locations() {
    }

    /**
     * Returns the center of the block containing {@code location} (floor + 0.5 on each axis).
     *
     * @param location source position
     * @return block-center coordinates with yaw and pitch preserved
     */
    public static IgnisLocation toCenter(IgnisLocation location) {
        double x = Math.floor(location.x()) + 0.5;
        double y = Math.floor(location.y()) + 0.5;
        double z = Math.floor(location.z()) + 0.5;
        return new IgnisLocation(location.worldId(), location.worldName(), x, y, z, location.yaw(), location.pitch());
    }

    /**
     * Floors {@code location} to integer block coordinates.
     *
     * @param location source position
     * @return block-corner coordinates with yaw and pitch preserved
     */
    public static IgnisLocation toBlock(IgnisLocation location) {
        return new IgnisLocation(
                location.worldId(),
                location.worldName(),
                Math.floor(location.x()),
                Math.floor(location.y()),
                Math.floor(location.z()),
                location.yaw(),
                location.pitch());
    }
}
