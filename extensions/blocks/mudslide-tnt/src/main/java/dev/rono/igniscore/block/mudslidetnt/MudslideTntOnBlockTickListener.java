package dev.rono.igniscore.block.mudslidetnt;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;

final class MudslideTntOnBlockTickListener implements OnBlockTickListener {
    private final MudslideTntRuntime runtime;

    MudslideTntOnBlockTickListener(MudslideTntRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        runtime.behavior.onTick(event.instance());
    }
}
