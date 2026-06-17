package dev.rono.igniscore.block.miragetnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class MirageTntOnBlockTickListener implements OnBlockTickListener {
    private final MirageTntRuntime runtime;

    MirageTntOnBlockTickListener(MirageTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
