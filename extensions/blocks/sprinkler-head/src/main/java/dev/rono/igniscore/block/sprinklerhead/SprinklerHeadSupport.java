package dev.rono.igniscore.block.sprinklerhead;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class SprinklerHeadSupport {
    private SprinklerHeadSupport() {
    }

    static final Map<String, Boolean> ARMED = new ConcurrentHashMap<>();

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        if (!ARMED.getOrDefault(LinkedBlockRegistry.key(location), false)) {
            return;
        }
        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "waterRadius", 4);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                IgnisLocation soil = center.add(x, -1, z);
                if (world.getBlockMaterialKey(soil).toLowerCase().contains("farmland")) {
                    world.spawnParticle(soil.add(0.5, 1, 0.5), "FALLING_WATER", 2, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

