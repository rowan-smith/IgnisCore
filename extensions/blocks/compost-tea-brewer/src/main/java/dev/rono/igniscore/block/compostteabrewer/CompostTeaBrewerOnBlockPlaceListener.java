package dev.rono.igniscore.block.compostteabrewer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class CompostTeaBrewerOnBlockPlaceListener implements OnBlockPlaceListener {
    private final CompostTeaBrewerRuntime runtime;

    CompostTeaBrewerOnBlockPlaceListener(CompostTeaBrewerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), CompostTeaBrewerSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 45),
                () -> CompostTeaBrewerSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

