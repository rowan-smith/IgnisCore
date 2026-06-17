package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.event.BlockActivateEvent;
import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.block.nuke.NukeSupport;

final class NukeOnBlockActivateListener implements OnBlockActivateListener {
    private final IgnisStrategyContext context;

    NukeOnBlockActivateListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockActivate(BlockActivateEvent event) {
        IgnisLocation center = Locations.toCenter(event.instance().getLocation());
        context.effects().playSound(center, "BLOCK_BEACON_ACTIVATE", 2.0f, 0.6f);
    }
}

