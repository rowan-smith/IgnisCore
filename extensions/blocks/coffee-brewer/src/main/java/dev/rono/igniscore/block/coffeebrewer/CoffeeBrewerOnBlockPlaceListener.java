package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class CoffeeBrewerOnBlockPlaceListener implements OnBlockPlaceListener {
    private final CoffeeBrewerRuntime runtime;

    CoffeeBrewerOnBlockPlaceListener(CoffeeBrewerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), CoffeeBrewerSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 35),
                () -> CoffeeBrewerSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

