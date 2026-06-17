package dev.rono.igniscore.block.socketlamp;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class SocketLampSupport {
    private SocketLampSupport() {
    }

    static final Map<String, Integer> LIGHT_LEVEL = new ConcurrentHashMap<>();

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        int level = LIGHT_LEVEL.getOrDefault(LinkedBlockRegistry.key(location), 0);
        if (level <= 0) {
            return;
        }
        IgnisWorld world = worldAt(ctx, location);
        world.spawnParticle(Locations.toCenter(location), "END_ROD", Math.min(8, level / 2),
                0.3, 0.2, 0.3, 0.01);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

