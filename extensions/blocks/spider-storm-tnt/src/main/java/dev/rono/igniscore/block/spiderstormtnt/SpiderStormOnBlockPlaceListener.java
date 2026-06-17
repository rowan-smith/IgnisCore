package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;

final class SpiderStormOnBlockPlaceListener implements OnBlockPlaceListener {
    private final SpiderStormRuntime runtime;

    SpiderStormOnBlockPlaceListener(SpiderStormRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        IgnisLocation center = Locations.toCenter(event.block().location());
        IgnisWorld world = SpiderStormSupport.worldAt(runtime, center);
        world.spawnParticle(center, "SPORE_BLOSSOM_AIR", 18, 0.45, 0.45, 0.45, 0.01);
        world.spawnParticle(center, "SMOKE", 8, 0.3, 0.3, 0.3, 0.01);
    }
}

