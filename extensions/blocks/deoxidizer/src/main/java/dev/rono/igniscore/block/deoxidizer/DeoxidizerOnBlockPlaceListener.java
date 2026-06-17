package dev.rono.igniscore.block.deoxidizer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class DeoxidizerOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    DeoxidizerOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 20);
        PlacedTickSupport.start(context, event.block().location(), period, () -> DeoxidizerSupport.tick(context, event.block().definition(), event.block().location()));
        IgnisLocation center = Locations.toCenter(event.block().location());
        TheatricsSupport.chime(DeoxidizerSupport.worldAt(context, center), center, 1.0f);
    }
}

