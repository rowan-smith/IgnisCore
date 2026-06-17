package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class NukeOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    NukeOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        NukeSupport.playCountdown(context, event.instance());
        NukeSupport.spawnFuseParticles(context, event.instance());
    }
}

