package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.icecreamfreezer.IceCreamFreezerSupport;

final class IceCreamFreezerOnBlockBreakListener implements OnBlockBreakListener {
    private final IceCreamFreezerRuntime runtime;

    IceCreamFreezerOnBlockBreakListener(IceCreamFreezerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

