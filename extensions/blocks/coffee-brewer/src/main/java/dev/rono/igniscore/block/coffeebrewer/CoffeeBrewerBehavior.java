package dev.rono.igniscore.block.coffeebrewer;

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

final class CoffeeBrewerBehavior {
    private static final int COCOA_SLOT = 11;
    private static final int BOTTLE_SLOT = 15;
    private static final int OUTPUT_SLOT = 17;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    CoffeeBrewerBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "coffee-brewer");
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        registry.registerBlock(location, title(definition), 3);
        PlacedTickSupport.start(context, location, StrategySupport.customInt(definition, "tickPeriod", 35),
                () -> tick(definition, location));
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
        registry.unregister(location);
    }

    void onPlacedInteract(BlockDefinition definition, IgnisLocation location, IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction, IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        if (heldItem != null && !heldItem.isAir() && ProcessingGuiSupport.matches(heldItem, "potion", "glass_bottle")) {
            player.applyPotionEffect("SPEED", 300, 0);
            heldItem.setAmount(heldItem.getAmount() - 1);
            player.sendMessage("<gold>Coffee boosts your speed.</gold>");
        }
        registry.openBlock(player, location);
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        var gui = registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(COCOA_SLOT), "cocoa")
                || !ProcessingGuiSupport.matches(inventory.getItem(BOTTLE_SLOT), "glass_bottle", "potion")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, COCOA_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, BOTTLE_SLOT);
        ProcessingGuiSupport.setOutput(context.extensions(), inventory, OUTPUT_SLOT, "potion", 1);
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "DRIPPING_HONEY", 5, 0.2, 0.3, 0.2, 0.01);
        world.playSound(center, "BLOCK_BREWING_STAND_BREW", 0.6f, 1.2f);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Coffee Brewer") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
