package dev.rono.igniscore.block.antigravityzone;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class AntiGravityZoneOnBlockTriggerListener implements OnBlockTriggerListener {
    private final AntiGravityZoneRuntime runtime;

    AntiGravityZoneOnBlockTriggerListener(AntiGravityZoneRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
