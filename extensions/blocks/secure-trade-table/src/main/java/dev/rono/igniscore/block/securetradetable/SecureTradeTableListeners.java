package dev.rono.igniscore.block.securetradetable;

import dev.rono.extensions.shared.gui.SecureTradeRegistry;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class SecureTradeTableListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private final IgnisStrategyContext context;
    private final SecureTradeRegistry registry;

    SecureTradeTableListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new SecureTradeRegistry(context);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Secure Trade") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.register(event.block().location(), title(event.block().definition()));
                TheatricsSupport.sparkle(worldAt(event.block().location()), Locations.toCenter(event.block().location()), "HAPPY_VILLAGER", 6);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                registry.unregister(event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() != CustomBlockAction.OPEN) {
                    return;
                }
                registry.open(event.player(), event.block().location());
                IgnisWorld world = worldAt(event.block().location());
                IgnisLocation center = Locations.toCenter(event.block().location());
                TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 8);
                world.playSound(center, "ENTITY_VILLAGER_TRADE", 0.9f, 1.0f);
                event.player().sendMessage("<gray>Place offers and confirm with <lime>lime dye</lime>.</gray>");
    }
}
