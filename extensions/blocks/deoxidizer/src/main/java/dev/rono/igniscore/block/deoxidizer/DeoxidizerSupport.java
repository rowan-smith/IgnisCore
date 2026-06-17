package dev.rono.igniscore.block.deoxidizer;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class DeoxidizerSupport {
    private DeoxidizerSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "deoxidizeRadius", 4);
          BlockScanSupport.deoxidizeCopper(world, center, radius);
          world.playSound(center, "ITEM_AXE_WAX_ON", 0.5f, 1.0f);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

