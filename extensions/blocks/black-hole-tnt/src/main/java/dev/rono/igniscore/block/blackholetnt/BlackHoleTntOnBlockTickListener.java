package dev.rono.igniscore.block.blackholetnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class BlackHoleTntOnBlockTickListener implements OnBlockTickListener {
    private final BlackHoleTntRuntime runtime;

    BlackHoleTntOnBlockTickListener(BlackHoleTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
