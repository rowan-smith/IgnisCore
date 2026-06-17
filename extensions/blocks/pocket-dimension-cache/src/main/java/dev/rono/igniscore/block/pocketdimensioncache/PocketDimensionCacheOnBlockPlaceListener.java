package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.util.Locations;

final class PocketDimensionCacheOnBlockPlaceListener implements OnBlockPlaceListener {
    private final PocketDimensionCacheRuntime runtime;

    PocketDimensionCacheOnBlockPlaceListener(PocketDimensionCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerPerPlayer(event.block().location(), PocketDimensionCacheSupport.title(runtime, event.block().definition()), PocketDimensionCacheSupport.rows(runtime, event.block().definition()));
        IgnisLocation center = Locations.toCenter(event.block().location());
        TheatricsSupport.chime(PocketDimensionCacheSupport.worldAt(runtime, center), center, 1.0f);
    }
}

