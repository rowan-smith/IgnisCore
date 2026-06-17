package dev.rono.igniscore.block.glitchtnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class GlitchTntOnBlockTickListener implements OnBlockTickListener {
    private final GlitchTntRuntime runtime;

    GlitchTntOnBlockTickListener(GlitchTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
