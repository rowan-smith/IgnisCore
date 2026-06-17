package dev.rono.igniscore.block.bouncingbetty;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class BouncingBettyOnBlockPlaceListener implements OnBlockPlaceListener {
    private final BouncingBettyRuntime runtime;

    BouncingBettyOnBlockPlaceListener(BouncingBettyRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
