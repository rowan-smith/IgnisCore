package dev.rono.igniscore.block.irrigationsprinkler;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.irrigationsprinkler.IrrigationSprinklerSupport;

final class IrrigationSprinklerOnBlockInteractListener implements OnBlockInteractListener {
    private final IrrigationSprinklerRuntime runtime;

    IrrigationSprinklerOnBlockInteractListener(IrrigationSprinklerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

