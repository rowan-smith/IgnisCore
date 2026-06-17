package dev.rono.igniscore.block.solarflaretnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class SolarFlareTntOnBlockTickListener implements OnBlockTickListener {
    private final SolarFlareTntRuntime runtime;

    SolarFlareTntOnBlockTickListener(SolarFlareTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
