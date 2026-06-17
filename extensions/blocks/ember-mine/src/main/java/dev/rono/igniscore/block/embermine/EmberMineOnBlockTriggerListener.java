package dev.rono.igniscore.block.embermine;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class EmberMineOnBlockTriggerListener implements OnBlockTriggerListener {
    private final EmberMineRuntime runtime;

    EmberMineOnBlockTriggerListener(EmberMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
