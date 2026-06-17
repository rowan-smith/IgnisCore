package dev.rono.igniscore.block.saplingnursery;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class SaplingNurseryOnBlockPlaceListener implements OnBlockPlaceListener {
    private final SaplingNurseryRuntime runtime;

    SaplingNurseryOnBlockPlaceListener(SaplingNurseryRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), SaplingNurserySupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 80),
                () -> SaplingNurserySupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

