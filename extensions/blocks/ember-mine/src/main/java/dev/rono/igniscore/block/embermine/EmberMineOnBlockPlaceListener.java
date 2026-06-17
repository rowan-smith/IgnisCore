package dev.rono.igniscore.block.embermine;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class EmberMineOnBlockPlaceListener implements OnBlockPlaceListener {
    private final EmberMineRuntime runtime;

    EmberMineOnBlockPlaceListener(EmberMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
