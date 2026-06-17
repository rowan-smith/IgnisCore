package dev.rono.igniscore.block.tsunamicharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class TsunamiChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final TsunamiChargeRuntime runtime;

    TsunamiChargeOnBlockTriggerListener(TsunamiChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
