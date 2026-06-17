package dev.rono.igniscore.block.prepcounter;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.prepcounter.PrepCounterSupport;

final class PrepCounterOnBlockInteractListener implements OnBlockInteractListener {
    private final PrepCounterRuntime runtime;

    PrepCounterOnBlockInteractListener(PrepCounterRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

