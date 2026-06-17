package dev.rono.igniscore.block.tethertnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class TetherTntBehavior {
    private static final Map<UUID, Map<Object, IgnisLocation>> SNAPSHOTS = new ConcurrentHashMap<>();
    private final IgnisStrategyContext context;

    TetherTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        double radius = StrategySupport.customDouble(def, "tetherRadius", 7.0);
        SNAPSHOTS.putIfAbsent(instance.getUuid(), EntityPhysicsSupport.snapshotPositions(worldAt(center), center, radius));
        if (instance.getTicksLeft() % 8 == 0) {
            worldAt(center).spawnParticle(center, "SOUL_FIRE_FLAME", 6, radius * 0.25, 0.3, radius * 0.25, 0.01);
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double radius = StrategySupport.customDouble(instance.getDefinition(), "tetherRadius", 7.0);
        Map<Object, IgnisLocation> anchors = SNAPSHOTS.remove(instance.getUuid());
        if (anchors != null) {
            EntityPhysicsSupport.snapDamage(world, anchors, loc, radius);
        }
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.3f, 0.9f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 3.5, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
