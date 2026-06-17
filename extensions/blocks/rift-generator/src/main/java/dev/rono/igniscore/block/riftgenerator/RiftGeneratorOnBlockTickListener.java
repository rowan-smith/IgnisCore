package dev.rono.igniscore.block.riftgenerator;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class RiftGeneratorOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    RiftGeneratorOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = RiftGeneratorSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        double pull = StrategySupport.customDouble(def, "riftPull", 0.12);
        for (Object entity : world.getNearbyEntities(loc, 6.0)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = loc.x() - entityLoc.x();
            double dy = loc.y() - entityLoc.y();
            double dz = loc.z() - entityLoc.z();
            double dist = Math.max(0.2, Math.sqrt(dx * dx + dy * dy + dz * dz));
            world.setEntityVelocity(entity, dx / dist * pull, dy / dist * pull, dz / dist * pull);
        }
        world.spawnParticle(loc, "PORTAL", 10, 0.4, 0.5, 0.4, 0.08);
    }
}

