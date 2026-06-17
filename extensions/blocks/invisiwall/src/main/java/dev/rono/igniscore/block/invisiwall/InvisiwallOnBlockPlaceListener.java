package dev.rono.igniscore.block.invisiwall;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class InvisiwallOnBlockPlaceListener implements OnBlockPlaceListener {
    private final InvisiwallRuntime runtime;

    InvisiwallOnBlockPlaceListener(InvisiwallRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
