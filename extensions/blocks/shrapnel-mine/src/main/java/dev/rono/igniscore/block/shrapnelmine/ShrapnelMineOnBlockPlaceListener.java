package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class ShrapnelMineOnBlockPlaceListener implements OnBlockPlaceListener {
    private final ShrapnelMineRuntime runtime;

    ShrapnelMineOnBlockPlaceListener(ShrapnelMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
