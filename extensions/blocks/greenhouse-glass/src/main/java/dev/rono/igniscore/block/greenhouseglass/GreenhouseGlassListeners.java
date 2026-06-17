package dev.rono.igniscore.block.greenhouseglass;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class GreenhouseGlassListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    GreenhouseGlassListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "greenhouseRadius", 2);
        if (!hasGlassRoof(world, center, radius)) {
            return;
        }
        BlockScanSupport.bonemealRadius(world, center, radius);
        TheatricsSupport.sparkle(world, center.add(0, 1, 0), "HAPPY_VILLAGER",
                StrategySupport.customInt(definition, "growthParticles", 4));
    }

    private boolean hasGlassRoof(IgnisWorld world, IgnisLocation center, int radius) {
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

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 60),
                        () -> tick(event.block().definition(), event.block().location()));
                TheatricsSupport.chime(worldAt(event.block().location()), Locations.toCenter(event.block().location()), 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
    }
}
