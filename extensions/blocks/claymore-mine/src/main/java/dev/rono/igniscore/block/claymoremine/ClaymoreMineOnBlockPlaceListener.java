package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class ClaymoreMineOnBlockPlaceListener implements OnBlockPlaceListener {
    private final ClaymoreMineRuntime runtime;

    ClaymoreMineOnBlockPlaceListener(ClaymoreMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
