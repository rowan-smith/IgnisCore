package dev.rono.igniscore.block.centrifugetnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class CentrifugeTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final CentrifugeTntRuntime runtime;

    CentrifugeTntOnBlockTriggerListener(CentrifugeTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
