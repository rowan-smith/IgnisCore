package dev.rono.igniscore.block.kelpcompressor;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class KelpCompressorSupport {
    private KelpCompressorSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "BUBBLE", StrategySupport.customInt(definition, "kelpBubbles", 6), 0.3, 0.2, 0.3, 0.02);
          world.playSound(center, "BLOCK_WET_GRASS_BREAK", 0.4f, 0.8f);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

