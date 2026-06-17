package dev.rono.igniscore.block.irrigationsprinkler;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.irrigationsprinkler.IrrigationSprinklerSupport;

final class IrrigationSprinklerOnBlockBreakListener implements OnBlockBreakListener {
    private final IrrigationSprinklerRuntime runtime;

    IrrigationSprinklerOnBlockBreakListener(IrrigationSprinklerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

