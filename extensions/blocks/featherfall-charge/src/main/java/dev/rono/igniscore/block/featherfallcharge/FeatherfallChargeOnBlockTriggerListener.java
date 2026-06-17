package dev.rono.igniscore.block.featherfallcharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class FeatherfallChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final FeatherfallChargeRuntime runtime;

    FeatherfallChargeOnBlockTriggerListener(FeatherfallChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
