package dev.rono.igniscore.block.blackholetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockTransformSupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class BlackHoleTntBehavior {
    private final IgnisStrategyContext context;

    BlackHoleTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        double pullRadius = StrategySupport.customDouble(def, "pullRadius", 10.0);
        double strength = StrategySupport.customDouble(def, "pullStrength", 0.45);
        EntityPhysicsSupport.pullToward(worldAt(center), center, pullRadius, strength);
        worldAt(center).spawnParticle(center, "PORTAL", 6, pullRadius * 0.2, 0.3, pullRadius * 0.2, 0.02);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int voidRadius = StrategySupport.customInt(def, "voidRadius", 4);
        boolean bedrockShell = StrategySupport.customBoolean(def, "bedrockShell", true);
        world.playSound(loc, "ENTITY_ENDERMAN_TELEPORT", 2.0f, 0.3f);
        world.spawnParticle(loc, "REVERSE_PORTAL", 40, voidRadius * 0.5, voidRadius * 0.5, voidRadius * 0.5, 0.05);
        BlockTransformSupport.blackHoleCollapse(context.region(), world, loc, voidRadius, bedrockShell, context.scheduler());
        world.createExplosion(loc, 1.5f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
