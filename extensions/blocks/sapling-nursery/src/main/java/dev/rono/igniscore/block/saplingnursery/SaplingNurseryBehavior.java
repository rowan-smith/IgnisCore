package dev.rono.igniscore.block.saplingnursery;

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
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class SaplingNurseryBehavior {
    private static final String[] SAPLINGS = {
            "oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling",
            "acacia_sapling", "dark_oak_sapling", "cherry_sapling", "mangrove_propagule", "azalea"
    };

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    SaplingNurseryBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "sapling-nursery");
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        registry.registerBlock(location, title(definition), 3);
        PlacedTickSupport.start(context, location, StrategySupport.customInt(definition, "tickPeriod", 80),
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
        IgnisWorld world = worldAt(location);
        IgnisLocation block = Locations.toBlock(location);
        for (int i = 0; i < SAPLINGS.length; i++) {
            IgnisItem sapling = gui.inventory().getItem(i);
            if (!ProcessingGuiSupport.matches(sapling, "sapling", "propagule", "azalea")) {
                continue;
            }
            IgnisLocation[] offsets = {block.add(1, 0, 0), block.add(-1, 0, 0), block.add(0, 0, 1), block.add(0, 0, -1)};
            for (IgnisLocation soil : offsets) {
                String below = world.getBlockMaterialKey(soil).toLowerCase();
                String above = world.getBlockMaterialKey(soil.add(0, 1, 0)).toLowerCase();
                if ((below.contains("dirt") || below.contains("grass")) && above.contains("air")) {
                    world.setBlockMaterialKey(soil.add(0, 1, 0), sapling.getMaterialKey());
                    ProcessingGuiSupport.consumeOne(gui.inventory(), i);
                    world.spawnParticle(soil.add(0.5, 1.5, 0.5), "HAPPY_VILLAGER", 4, 0.2, 0.2, 0.2, 0.01);
                    world.playSound(soil, "BLOCK_GRASS_PLACE", 0.7f, 1.0f);
                    return;
                }
            }
        }
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Sapling Nursery") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
