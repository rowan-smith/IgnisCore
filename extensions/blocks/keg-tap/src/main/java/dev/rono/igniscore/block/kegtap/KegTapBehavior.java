package dev.rono.igniscore.block.kegtap;

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

final class KegTapBehavior {
    private static final int BUCKET_SLOT = 10;
    private static final int OUTPUT_SLOT = 16;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    KegTapBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "keg-tap");
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
        if (!ProcessingGuiSupport.matches(inventory.getItem(BUCKET_SLOT), "water_bucket", "milk_bucket")) {
            return;
        }
        IgnisItem out = inventory.getItem(OUTPUT_SLOT);
        if (out != null && !out.isAir()) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, BUCKET_SLOT);
        ProcessingGuiSupport.setOutput(context.extensions(), inventory, OUTPUT_SLOT, "potion", 1);
        IgnisWorld world = worldAt(location);
        world.playSound(Locations.toCenter(location), "ITEM_BOTTLE_FILL", 0.6f, 1.0f);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Keg Tap") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
