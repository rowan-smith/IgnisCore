package dev.rono.igniscore.api.util;

import dev.rono.igniscore.api.port.IgnisLocation;

public final class Locations {

    private Locations() {
    }

    public static IgnisLocation toCenter(IgnisLocation location) {
        double x = Math.floor(location.x()) + 0.5;
        double y = Math.floor(location.y()) + 0.5;
        double z = Math.floor(location.z()) + 0.5;
        return new IgnisLocation(location.worldId(), location.worldName(), x, y, z, location.yaw(), location.pitch());
    }

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
