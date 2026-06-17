package dev.rono.igniscore.block.securetradetable;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;

final class SecureTradeTableOnBlockInteractListener implements OnBlockInteractListener {
    private final SecureTradeTableRuntime runtime;

    SecureTradeTableOnBlockInteractListener(SecureTradeTableRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        runtime.registry.open(event.player(), event.block().location());
        IgnisWorld world = SecureTradeTableSupport.worldAt(runtime, event.block().location());
        IgnisLocation center = Locations.toCenter(event.block().location());
        TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 8);
        world.playSound(center, "ENTITY_VILLAGER_TRADE", 0.9f, 1.0f);
        event.player().sendMessage("<gray>Place offers and confirm with <lime>lime dye</lime>.</gray>");
    }
}

