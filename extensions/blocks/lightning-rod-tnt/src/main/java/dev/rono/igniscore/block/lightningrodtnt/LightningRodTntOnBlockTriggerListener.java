package dev.rono.igniscore.block.lightningrodtnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class LightningRodTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final LightningRodTntRuntime runtime;

    LightningRodTntOnBlockTriggerListener(LightningRodTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
