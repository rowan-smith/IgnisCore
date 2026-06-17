package dev.rono.igniscore.block.scarecharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class ScareChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final ScareChargeRuntime runtime;

    ScareChargeOnBlockTriggerListener(ScareChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
