package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
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
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class PiglinBarterPostListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private static final int INPUT_SLOT = 10;
    private static final int OUTPUT_START = 14;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    PiglinBarterPostListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "piglin-barter-post");
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        registry.registerBlock(location, title(definition), 3);
        PlacedTickSupport.start(context, location, StrategySupport.customInt(definition, "tickPeriod", 30),
                () -> tick(definition, location));
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
        registry.unregister(location);
    }

    void onPlacedInteract(BlockDefinition definition, IgnisLocation location, IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction, IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action == CustomBlockAction.OPEN) {
            registry.openBlock(player, location);
        }
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        var gui = registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        IgnisItem input = inventory.getItem(INPUT_SLOT);
        if (!ProcessingGuiSupport.matches(input, "gold_ingot")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, INPUT_SLOT);
        String[] loot = {"ender_pearl", "crying_obsidian", "spectral_arrow", "gilded_blackstone", "iron_nugget"};
        String reward = loot[(int) (Math.random() * loot.length)];
        ProcessingGuiSupport.setOutput(context.extensions(), inventory, OUTPUT_START, reward, 1);
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "CRIMSON_SPORE", 8);
        world.playSound(center, "ENTITY_PIGLIN_ADMIRING_ITEM", 0.8f, 1.0f);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Piglin Barter Post") : definition.getTitle();
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
