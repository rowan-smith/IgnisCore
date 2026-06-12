package dev.rono.blocks.quarrycache;

import org.bukkit.Location;

record QuarryCacheData(Location location, double collectRadius, double collectDepth, boolean showIndicator, QuarryCacheInventory inventory) {
    QuarryCacheData(Location location, double collectRadius, double collectDepth, boolean showIndicator, QuarryCacheInventory inventory) {
        this.location = location.getBlock().getLocation();
        this.collectRadius = collectRadius;
        this.collectDepth = collectDepth;
        this.showIndicator = showIndicator;
        this.inventory = inventory;
    }

    Location center() {
        return location.clone().add(0.5, 0.5, 0.5);
    }

    boolean isWithinRadius(Location target) {
        if (location.getWorld() == null || target.getWorld() == null || !location.getWorld().equals(target.getWorld())) {
            return false;
        }

        double centerX = location.getX() + 0.5;
        double centerY = location.getY() + 0.5;
        double centerZ = location.getZ() + 0.5;
        double dx = target.getX() - centerX;
        double dz = target.getZ() - centerZ;

        if ((dx * dx) + (dz * dz) > collectRadius * collectRadius) {
            return false;
        }

        double dy = Math.abs(target.getY() - centerY);
        return dy <= collectDepth;
    }
}
