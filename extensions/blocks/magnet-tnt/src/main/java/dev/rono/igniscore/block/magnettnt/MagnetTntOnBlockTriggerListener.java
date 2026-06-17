package dev.rono.igniscore.block.magnettnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class MagnetTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final MagnetTntRuntime runtime;

    MagnetTntOnBlockTriggerListener(MagnetTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
