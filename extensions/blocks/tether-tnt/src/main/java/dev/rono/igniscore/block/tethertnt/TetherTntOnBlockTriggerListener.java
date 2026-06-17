package dev.rono.igniscore.block.tethertnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class TetherTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final TetherTntRuntime runtime;

    TetherTntOnBlockTriggerListener(TetherTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
