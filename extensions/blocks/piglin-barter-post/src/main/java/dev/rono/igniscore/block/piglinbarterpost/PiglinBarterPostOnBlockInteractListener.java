package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.piglinbarterpost.PiglinBarterPostSupport;

final class PiglinBarterPostOnBlockInteractListener implements OnBlockInteractListener {
    private final PiglinBarterPostRuntime runtime;

    PiglinBarterPostOnBlockInteractListener(PiglinBarterPostRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

