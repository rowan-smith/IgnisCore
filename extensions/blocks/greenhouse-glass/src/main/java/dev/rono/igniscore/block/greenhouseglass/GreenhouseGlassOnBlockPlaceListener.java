package dev.rono.igniscore.block.greenhouseglass;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class GreenhouseGlassOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    GreenhouseGlassOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 60),
                () -> GreenhouseGlassSupport.tick(context, event.block().definition(), event.block().location()));
        TheatricsSupport.chime(GreenhouseGlassSupport.worldAt(context, event.block().location()), Locations.toCenter(event.block().location()), 1.0f);
    }
}

