package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.coffeebrewer.CoffeeBrewerSupport;

final class CoffeeBrewerOnBlockBreakListener implements OnBlockBreakListener {
    private final CoffeeBrewerRuntime runtime;

    CoffeeBrewerOnBlockBreakListener(CoffeeBrewerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

