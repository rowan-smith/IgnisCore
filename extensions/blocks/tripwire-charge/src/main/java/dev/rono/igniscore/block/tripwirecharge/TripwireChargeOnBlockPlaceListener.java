package dev.rono.igniscore.block.tripwirecharge;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class TripwireChargeOnBlockPlaceListener implements OnBlockPlaceListener {
    private final TripwireChargeRuntime runtime;

    TripwireChargeOnBlockPlaceListener(TripwireChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
