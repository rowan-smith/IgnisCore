package dev.rono.igniscore.block.fortunecookiemaker;

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

final class FortuneCookieMakerBehavior {
    private static final int WHEAT_SLOT = 11;
    private static final int PAPER_SLOT = 15;
    private static final int OUTPUT_SLOT = 17;
    private static final String[] FORTUNES = {
            "A surprise awaits behind the next door.",
            "Beware the creeper in plain sight.",
            "Your build will inspire the server.",
            "Share loot and luck will follow.",
            "Dig down — but not too far."
    };

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    FortuneCookieMakerBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "fortune-cookie-maker");
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
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        if (heldItem != null && !heldItem.isAir() && ProcessingGuiSupport.matches(heldItem, "cookie")) {
            String fortune = FORTUNES[(int) (Math.random() * FORTUNES.length)];
            player.sendMessage("<gold>Fortune:</gold> <italic>" + fortune + "</italic>");
            TheatricsSupport.sparkle(worldAt(location), player.getLocation(), "NOTE", 6);
        }
        registry.openBlock(player, location);
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        var gui = registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(WHEAT_SLOT), "wheat")
                || !ProcessingGuiSupport.matches(inventory.getItem(PAPER_SLOT), "paper")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, WHEAT_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, PAPER_SLOT);
        ProcessingGuiSupport.setOutput(context.extensions(), inventory, OUTPUT_SLOT, "cookie", 1);
        IgnisWorld world = worldAt(location);
        world.playSound(Locations.toCenter(location), "ENTITY_GENERIC_EAT", 0.5f, 1.3f);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Fortune Cookie Maker") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
