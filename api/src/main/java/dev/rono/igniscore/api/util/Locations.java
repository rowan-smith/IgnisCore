package dev.rono.igniscore.api.util;

import org.bukkit.Location;

public final class Locations {

    private Locations() {
    }

    public static Location toCenter(Location location) {
        Location centered = location.clone();
        centered.setX(location.getBlockX() + 0.5);
        centered.setY(location.getBlockY() + 0.5);
        centered.setZ(location.getBlockZ() + 0.5);
        return centered;
    }
}
