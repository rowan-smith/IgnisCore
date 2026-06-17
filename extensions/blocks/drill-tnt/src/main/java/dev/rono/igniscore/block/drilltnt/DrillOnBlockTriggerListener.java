package dev.rono.igniscore.block.drilltnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class DrillOnBlockTriggerListener implements OnBlockTriggerListener {
    private final DrillRuntime runtime;

    DrillOnBlockTriggerListener(DrillRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
