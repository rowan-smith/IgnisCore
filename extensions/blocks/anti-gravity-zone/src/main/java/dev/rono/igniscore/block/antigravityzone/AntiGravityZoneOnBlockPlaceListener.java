package dev.rono.igniscore.block.antigravityzone;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class AntiGravityZoneOnBlockPlaceListener implements OnBlockPlaceListener {
    private final AntiGravityZoneRuntime runtime;

    AntiGravityZoneOnBlockPlaceListener(AntiGravityZoneRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.behavior.onPlaced(event.block().definition(), event.block().location());
    }
}
