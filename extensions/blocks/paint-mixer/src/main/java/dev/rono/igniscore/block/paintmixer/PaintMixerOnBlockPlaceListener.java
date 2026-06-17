package dev.rono.igniscore.block.paintmixer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class PaintMixerOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    PaintMixerOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 20);
        PlacedTickSupport.start(context, event.block().location(), period, () -> PaintMixerSupport.tick(context, event.block().definition(), event.block().location()));
        IgnisLocation center = Locations.toCenter(event.block().location());
        TheatricsSupport.chime(PaintMixerSupport.worldAt(context, center), center, 1.0f);
    }
}

