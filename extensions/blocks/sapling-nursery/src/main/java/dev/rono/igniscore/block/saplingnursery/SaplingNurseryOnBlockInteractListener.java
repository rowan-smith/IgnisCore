package dev.rono.igniscore.block.saplingnursery;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.saplingnursery.SaplingNurserySupport;

final class SaplingNurseryOnBlockInteractListener implements OnBlockInteractListener {
    private final SaplingNurseryRuntime runtime;

    SaplingNurseryOnBlockInteractListener(SaplingNurseryRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

