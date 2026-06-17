package dev.rono.igniscore.block.trampolinetnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class TrampolineTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final TrampolineTntRuntime runtime;

    TrampolineTntOnBlockTriggerListener(TrampolineTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
