package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.util.Locations;

record QuarryCacheData(IgnisLocation location, double collectRadius, double collectDepth, boolean showIndicator,
                       QuarryCacheInventory inventory) {
    QuarryCacheData {
        location = Locations.toBlock(location);
    }

    IgnisLocation center() {
        return Locations.toCenter(location);
    }

    boolean isWithinRadius(IgnisLocation target) {
        if (location.worldName() != null && target.worldName() != null
                && !location.worldName().equals(target.worldName())) {
            return false;
        }
        if (location.worldId() != null && target.worldId() != null
                && !location.worldId().equals(target.worldId())) {
            return false;
        }

        double centerX = location.x() + 0.5;
        double centerY = location.y() + 0.5;
        double centerZ = location.z() + 0.5;
        double dx = target.x() - centerX;
        double dz = target.z() - centerZ;

        if ((dx * dx) + (dz * dz) > collectRadius * collectRadius) {
            return false;
        }

        double dy = Math.abs(target.y() - centerY);
        return dy <= collectDepth;
    }
}
