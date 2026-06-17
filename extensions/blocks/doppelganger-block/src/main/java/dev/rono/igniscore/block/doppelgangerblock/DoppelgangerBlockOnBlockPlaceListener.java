package dev.rono.igniscore.block.doppelgangerblock;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class DoppelgangerBlockOnBlockPlaceListener implements OnBlockPlaceListener {
    private final DoppelgangerBlockRuntime runtime;

    DoppelgangerBlockOnBlockPlaceListener(DoppelgangerBlockRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
