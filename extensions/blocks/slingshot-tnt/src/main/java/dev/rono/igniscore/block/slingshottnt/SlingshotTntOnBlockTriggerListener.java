package dev.rono.igniscore.block.slingshottnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class SlingshotTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final SlingshotTntRuntime runtime;

    SlingshotTntOnBlockTriggerListener(SlingshotTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
