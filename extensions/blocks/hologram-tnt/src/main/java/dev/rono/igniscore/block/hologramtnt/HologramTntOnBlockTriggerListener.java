package dev.rono.igniscore.block.hologramtnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class HologramTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final HologramTntRuntime runtime;

    HologramTntOnBlockTriggerListener(HologramTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
