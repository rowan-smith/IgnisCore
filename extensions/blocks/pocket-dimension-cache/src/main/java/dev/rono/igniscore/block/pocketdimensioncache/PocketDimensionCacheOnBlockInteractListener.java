package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;

final class PocketDimensionCacheOnBlockInteractListener implements OnBlockInteractListener {
    private final PocketDimensionCacheRuntime runtime;

    PocketDimensionCacheOnBlockInteractListener(PocketDimensionCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        runtime.registry.openPerPlayer(event.player(), event.block().location(), PocketDimensionCacheSupport.title(runtime, event.block().definition()), PocketDimensionCacheSupport.rows(runtime, event.block().definition()));
        IgnisWorld world = PocketDimensionCacheSupport.worldAt(runtime, event.block().location());
        IgnisLocation center = Locations.toCenter(event.block().location());
        TheatricsSupport.sparkle(world, center, "PORTAL", 10);
        world.playSound(center, "BLOCK_ENDER_CHEST_OPEN", 0.7f, 1.1f);
        event.player().sendMessage("<light_purple>Opened your pocket dimension cache.</light_purple>");
    }
}

