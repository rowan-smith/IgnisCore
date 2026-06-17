package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class NukeOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    NukeOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        IgnisLocation center = Locations.toCenter(event.block().location());
        IgnisWorld world = NukeSupport.worldAt(context, center);
        world.spawnParticle(center, "FLAME", 16, 0.35, 0.35, 0.35, 0.02);
        world.spawnParticle(center, "SMOKE", 10, 0.3, 0.3, 0.3, 0.01);
    }
}

