package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class LostAndFoundBinOnBlockPlaceListener implements OnBlockPlaceListener {
    private final LostAndFoundBinRuntime runtime;

    LostAndFoundBinOnBlockPlaceListener(LostAndFoundBinRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), LostAndFoundBinSupport.title(runtime, event.block().definition()), 6);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 100),
                () -> LostAndFoundBinSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

