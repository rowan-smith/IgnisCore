package dev.rono.igniscore.block.greenhouseglass;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class GreenhouseGlassSupport {
    private GreenhouseGlassSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "greenhouseRadius", 2);
        if (!hasGlassRoof(ctx, world, center, radius)) {
            return;
        }
        BlockScanSupport.bonemealRadius(world, center, radius);
        TheatricsSupport.sparkle(world, center.add(0, 1, 0), "HAPPY_VILLAGER",
                StrategySupport.customInt(definition, "growthParticles", 4));
    
    }

    static boolean hasGlassRoof(IgnisStrategyContext ctx, IgnisWorld world, IgnisLocation center, int radius) {

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                String above = world.getBlockMaterialKey(center.add(x, 2, z)).toLowerCase();
                if (!above.contains("glass")) {
                    return false;
                }
            }
        }
        return true;
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

