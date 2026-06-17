package dev.rono.igniscore.block.tripwirecharge;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class TripwireChargeOnBlockBreakListener implements OnBlockBreakListener {
    private final TripwireChargeRuntime runtime;

    TripwireChargeOnBlockBreakListener(TripwireChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.behavior.onPlacedBreak(event.block().location());
    }
}
