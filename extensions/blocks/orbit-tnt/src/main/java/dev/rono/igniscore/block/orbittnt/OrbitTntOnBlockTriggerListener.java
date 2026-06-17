package dev.rono.igniscore.block.orbittnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class OrbitTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final OrbitTntRuntime runtime;

    OrbitTntOnBlockTriggerListener(OrbitTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
