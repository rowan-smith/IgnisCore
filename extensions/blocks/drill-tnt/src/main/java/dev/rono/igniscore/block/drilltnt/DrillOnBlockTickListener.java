package dev.rono.igniscore.block.drilltnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class DrillOnBlockTickListener implements OnBlockTickListener {
    private final DrillRuntime runtime;

    DrillOnBlockTickListener(DrillRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
