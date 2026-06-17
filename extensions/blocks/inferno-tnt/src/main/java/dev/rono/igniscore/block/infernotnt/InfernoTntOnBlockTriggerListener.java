package dev.rono.igniscore.block.infernotnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class InfernoTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final InfernoTntRuntime runtime;

    InfernoTntOnBlockTriggerListener(InfernoTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
