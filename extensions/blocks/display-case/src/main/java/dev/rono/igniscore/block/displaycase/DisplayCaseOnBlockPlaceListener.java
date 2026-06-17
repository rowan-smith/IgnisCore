package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class DisplayCaseOnBlockPlaceListener implements OnBlockPlaceListener {
    private final DisplayCaseRuntime runtime;

    DisplayCaseOnBlockPlaceListener(DisplayCaseRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), DisplayCaseSupport.title(runtime, event.block().definition()), 3);
    }
}

