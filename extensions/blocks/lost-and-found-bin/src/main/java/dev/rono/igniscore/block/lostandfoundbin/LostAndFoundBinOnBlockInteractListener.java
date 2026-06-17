package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.lostandfoundbin.LostAndFoundBinSupport;

final class LostAndFoundBinOnBlockInteractListener implements OnBlockInteractListener {
    private final LostAndFoundBinRuntime runtime;

    LostAndFoundBinOnBlockInteractListener(LostAndFoundBinRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

