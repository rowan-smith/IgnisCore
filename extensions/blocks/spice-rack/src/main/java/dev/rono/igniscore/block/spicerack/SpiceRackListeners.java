package dev.rono.igniscore.block.spicerack;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
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

final class SpiceRackListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private static final int FOOD_SLOT = 11;
    private static final int SPICE_SLOT = 15;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    SpiceRackListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "spice-rack");
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Spice Rack") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.registerBlock(event.block().location(), title(event.block().definition()), 3);
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
                registry.openBlock(event.player(), event.block().location());
                var gui = registry.blockGui(event.block().location());
                if (gui == null) {
                    return;
                }
                var inventory = gui.inventory();
                if (ProcessingGuiSupport.matches(inventory.getItem(FOOD_SLOT), "bread", "cooked", "apple", "carrot", "beef")
                        && ProcessingGuiSupport.matches(inventory.getItem(SPICE_SLOT), "spider_eye", "glow_berries", "sugar", "cocoa")) {
                    event.player().applyPotionEffect("HASTE", 200, 0);
                    event.player().applyPotionEffect("SATURATION", 100, 0);
                    ProcessingGuiSupport.consumeOne(inventory, SPICE_SLOT);
                    IgnisWorld world = worldAt(event.block().location());
                    IgnisLocation center = Locations.toCenter(event.block().location());
                    TheatricsSupport.sparkle(world, center, "FIREWORK", 6);
                    world.playSound(center, "ENTITY_GENERIC_EAT", 0.8f, 1.1f);
                    event.player().sendMessage("<gold>Spiced plate grants haste and saturation.</gold>");
                }
    }
}
