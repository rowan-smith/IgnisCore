package dev.rono.igniscore.block.acidtnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class AcidTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final AcidTntRuntime runtime;

    AcidTntOnBlockTriggerListener(AcidTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
