package dev.rono.igniscore.block.antigravityzone;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class AntiGravityZoneBehavior {
    private static final Map<String, IgnisTask> ACTIVE = new ConcurrentHashMap<>();
    private final IgnisStrategyContext context;

    AntiGravityZoneBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        String key = encode(location);
        stop(key);
        double radius = StrategySupport.customDouble(definition, "bubbleRadius", 6.0);
        double lift = StrategySupport.customDouble(definition, "liftStrength", 0.18);
        IgnisTask[] ref = {null};
        ref[0] = context.scheduler().runRepeating(location, () -> {
            IgnisWorld world = worldAt(location);
            IgnisLocation center = Locations.toCenter(location);
            EntityPhysicsSupport.applyLevitation(world, center, radius, lift);
            world.spawnParticle(center, "CLOUD", 4, radius * 0.3, 0.2, radius * 0.3, 0.01);
        }, 5L, 5L);
        ACTIVE.put(key, ref[0]);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        stop(encode(instance.getLocation()));
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "BLOCK_GLASS_BREAK", 1.5f, 0.7f);
        world.spawnParticle(loc, "CLOUD", 30, 2, 1, 2, 0.05);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 2.5, false);
    }

    private void stop(String key) {
        IgnisTask task = ACTIVE.remove(key);
        if (task != null) {
            task.cancel();
        }
    }

    private String encode(IgnisLocation location) {
        return location.worldName() + ":" + (int) location.x() + ":" + (int) location.y() + ":" + (int) location.z();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
