package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class AutoSieveOnBlockPlaceListener implements OnBlockPlaceListener {
    private final AutoSieveBehavior behavior;

    AutoSieveOnBlockPlaceListener(AutoSieveBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        behavior.onPlaced(event.definition(), event.block());
    }
}
