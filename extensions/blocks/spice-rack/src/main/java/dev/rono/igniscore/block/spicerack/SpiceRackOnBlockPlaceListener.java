package dev.rono.igniscore.block.spicerack;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class SpiceRackOnBlockPlaceListener implements OnBlockPlaceListener {
    private final SpiceRackRuntime runtime;

    SpiceRackOnBlockPlaceListener(SpiceRackRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), SpiceRackSupport.title(runtime, event.block().definition()), 3);
    }
}

