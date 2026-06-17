package dev.rono.igniscore.block.glitchtnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;

final class GlitchTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final GlitchTntRuntime runtime;

    GlitchTntOnBlockTriggerListener(GlitchTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        runtime.behavior.onTrigger(event.instance(), event.triggerContext());
    }
}
