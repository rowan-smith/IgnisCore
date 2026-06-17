package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class IceCreamFreezerOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IceCreamFreezerRuntime runtime;

    IceCreamFreezerOnBlockPlaceListener(IceCreamFreezerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), IceCreamFreezerSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 60),
                () -> IceCreamFreezerSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

