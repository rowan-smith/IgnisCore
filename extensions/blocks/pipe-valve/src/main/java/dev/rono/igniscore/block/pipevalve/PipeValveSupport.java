package dev.rono.igniscore.block.pipevalve;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class PipeValveSupport {
    private PipeValveSupport() {
    }

    static final Map<String, Boolean> OPEN = new ConcurrentHashMap<>();

    static void tick(IgnisStrategyContext ctx, IgnisLocation location) {

        if (!OPEN.getOrDefault(LinkedBlockRegistry.key(location), false)) {
            return;
        }
        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "DRIPPING_WATER", 4, 0.2, 0.1, 0.2, 0.01);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

