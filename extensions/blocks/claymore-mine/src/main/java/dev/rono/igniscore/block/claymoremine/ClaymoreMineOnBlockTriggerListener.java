package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class ClaymoreMineOnBlockTriggerListener implements OnBlockTriggerListener {
    private final ClaymoreMineRuntime runtime;

    ClaymoreMineOnBlockTriggerListener(ClaymoreMineRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
