package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class ShrapnelMineOnBlockTriggerListener implements OnBlockTriggerListener {
    private final ShrapnelMineRuntime runtime;

    ShrapnelMineOnBlockTriggerListener(ShrapnelMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance());
    }
}
