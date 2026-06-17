package dev.rono.igniscore.block.tripwirecharge;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.PlacedMetaSupport;
import dev.rono.igniscore.api.util.Locations;

final class TripwireChargeBehavior {
    private final IgnisStrategyContext context;

    TripwireChargeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        IgnisLocation partner = PlacedMetaSupport.tripwirePartner(location);
        if (partner == null) {
            return;
        }
        double lineRadius = StrategySupport.customDouble(definition, "lineRadius", 1.2);
        context.scheduler().runRepeating(location, () -> watchLine(location, partner, lineRadius), 5L, 5L);
    }

    void onPlacedBreak(IgnisLocation location) {
        PlacedMetaSupport.clear(location);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.2f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 4.0, false);
    }

    private void watchLine(IgnisLocation a, IgnisLocation b, double radius) {
        IgnisWorld world = worldAt(a);
        IgnisLocation center = Locations.toCenter(a);
        for (Object entity : world.getNearbyEntities(center, distance(a, b) + 2)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            if (distanceToSegment(entityLoc, a, b) <= radius) {
                world.spawnParticle(entityLoc, "CRIT", 4, 0.1, 0.1, 0.1, 0.02);
                IgnisCoreAPI.ignitePlacedBlock(Locations.toBlock(a), null);
                IgnisCoreAPI.ignitePlacedBlock(Locations.toBlock(b), null);
                return;
            }
        }
    }

    private double distance(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private double distanceToSegment(IgnisLocation p, IgnisLocation a, IgnisLocation b) {
        double abx = b.x() - a.x();
        double aby = b.y() - a.y();
        double abz = b.z() - a.z();
        double apx = p.x() - a.x();
        double apy = p.y() - a.y();
        double apz = p.z() - a.z();
        double abLen2 = abx * abx + aby * aby + abz * abz;
        double t = abLen2 < 0.0001 ? 0 : Math.max(0, Math.min(1, (apx * abx + apy * aby + apz * abz) / abLen2));
        double cx = a.x() + t * abx;
        double cy = a.y() + t * aby;
        double cz = a.z() + t * abz;
        double dx = p.x() - cx;
        double dy = p.y() - cy;
        double dz = p.z() - cz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
