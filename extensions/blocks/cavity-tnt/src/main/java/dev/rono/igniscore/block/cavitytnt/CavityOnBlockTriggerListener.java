package dev.rono.igniscore.block.cavitytnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class CavityOnBlockTriggerListener implements OnBlockTriggerListener {
    private final CavityRuntime runtime;

    CavityOnBlockTriggerListener(CavityRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
