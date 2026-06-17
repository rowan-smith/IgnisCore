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

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        registry.register(location, title(definition));
        TheatricsSupport.sparkle(worldAt(location), Locations.toCenter(location), "HAPPY_VILLAGER", 6);
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        registry.unregister(location);
    }

    void onPlacedInteract(BlockDefinition definition,
                          IgnisLocation location,
                          IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction,
                          IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        registry.open(player, location);
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 8);
        world.playSound(center, "ENTITY_VILLAGER_TRADE", 0.9f, 1.0f);
        player.sendMessage("<gray>Place offers and confirm with <lime>lime dye</lime>.</gray>");
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Secure Trade") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        onPlaced(event.block().definition(), event.block().location());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        onPlacedBreak(event.block().definition(), event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        onPlacedInteract(event.block().definition(), event.block().location(), event.player(), event.interaction(), event.heldItem(), event.action());
    }
}
