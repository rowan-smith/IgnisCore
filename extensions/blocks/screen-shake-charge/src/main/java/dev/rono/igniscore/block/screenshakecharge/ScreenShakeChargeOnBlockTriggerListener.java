package dev.rono.igniscore.block.screenshakecharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class ScreenShakeChargeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final ScreenShakeChargeRuntime runtime;

    ScreenShakeChargeOnBlockTriggerListener(ScreenShakeChargeRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
