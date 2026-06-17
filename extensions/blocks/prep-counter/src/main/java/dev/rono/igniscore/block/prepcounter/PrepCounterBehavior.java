package dev.rono.igniscore.block.prepcounter;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class PrepCounterBehavior {
    private static final int[] INPUT_SLOTS = {10, 11, 12};
    private static final int OUTPUT_SLOT = 16;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    PrepCounterBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "prep-counter");
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        registry.registerBlock(location, title(definition), 3);
        PlacedTickSupport.start(context, location, StrategySupport.customInt(definition, "tickPeriod", 40),
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
        int foods = 0;
        for (int slot : INPUT_SLOTS) {
            IgnisItem item = inventory.getItem(slot);
            if (ProcessingGuiSupport.matches(item, "bread", "cooked", "apple", "carrot", "potato", "beef", "pork", "chicken", "fish")) {
                foods++;
            }
        }
        if (foods < 3) {
            return;
        }
        for (int slot : INPUT_SLOTS) {
            ProcessingGuiSupport.consumeOne(inventory, slot);
        }
        ProcessingGuiSupport.setOutput(context.getExtensionSupport(), inventory, OUTPUT_SLOT, "golden_carrot", 1);
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 10);
        world.playSound(center, "ENTITY_PLAYER_BURP", 0.5f, 1.2f);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Prep Counter") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
