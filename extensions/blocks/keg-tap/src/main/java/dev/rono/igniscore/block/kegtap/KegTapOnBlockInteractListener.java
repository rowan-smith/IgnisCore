package dev.rono.igniscore.block.kegtap;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.kegtap.KegTapSupport;

final class KegTapOnBlockInteractListener implements OnBlockInteractListener {
    private final KegTapRuntime runtime;

    KegTapOnBlockInteractListener(KegTapRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

