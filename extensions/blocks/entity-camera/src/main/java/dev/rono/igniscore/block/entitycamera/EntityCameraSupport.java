package dev.rono.igniscore.block.entitycamera;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class EntityCameraSupport {
    private EntityCameraSupport() {
    }

    static Object findNearestPassive(IgnisStrategyContext ctx, IgnisWorld world, IgnisLocation center, double radius) {

        Object nearest = null;
        double best = Double.MAX_VALUE;
        for (Object entity : world.getNearbyEntities(center, radius)) {
            if (!EntityUtilSupport.isPassive(entity)) {
                continue;
            }
            IgnisLocation loc = world.getEntityLocation(entity);
            if (loc == null) {
                continue;
            }
            double dx = loc.x() - center.x();
            double dy = loc.y() - center.y();
            double dz = loc.z() - center.z();
            double dist = dx * dx + dy * dy + dz * dz;
            if (dist < best) {
                best = dist;
                nearest = entity;
            }
        }
        return nearest;
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

