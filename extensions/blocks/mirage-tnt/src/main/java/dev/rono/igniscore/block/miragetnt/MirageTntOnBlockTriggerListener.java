package dev.rono.igniscore.block.miragetnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class MirageTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final MirageTntRuntime runtime;

    MirageTntOnBlockTriggerListener(MirageTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
