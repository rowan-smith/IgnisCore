package dev.rono.igniscore.block.prepcounter;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class PrepCounterOnBlockPlaceListener implements OnBlockPlaceListener {
    private final PrepCounterRuntime runtime;

    PrepCounterOnBlockPlaceListener(PrepCounterRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), PrepCounterSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 40),
                () -> PrepCounterSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

