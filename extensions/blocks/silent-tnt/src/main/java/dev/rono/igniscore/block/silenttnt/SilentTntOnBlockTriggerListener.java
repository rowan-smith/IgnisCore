package dev.rono.igniscore.block.silenttnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class SilentTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final SilentTntRuntime runtime;

    SilentTntOnBlockTriggerListener(SilentTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
