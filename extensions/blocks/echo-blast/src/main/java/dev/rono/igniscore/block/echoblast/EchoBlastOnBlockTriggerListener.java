package dev.rono.igniscore.block.echoblast;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class EchoBlastOnBlockTriggerListener implements OnBlockTriggerListener {
    private final EchoBlastRuntime runtime;

    EchoBlastOnBlockTriggerListener(EchoBlastRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
