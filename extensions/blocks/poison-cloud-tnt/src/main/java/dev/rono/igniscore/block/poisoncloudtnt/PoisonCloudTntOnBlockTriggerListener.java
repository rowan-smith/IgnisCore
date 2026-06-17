package dev.rono.igniscore.block.poisoncloudtnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class PoisonCloudTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final PoisonCloudTntRuntime runtime;

    PoisonCloudTntOnBlockTriggerListener(PoisonCloudTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
