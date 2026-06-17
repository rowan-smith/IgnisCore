package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class DisplayCaseBehavior {
    private static final int DISPLAY_SLOT = 13;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    DisplayCaseBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "display-case");
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        registry.registerBlock(location, title(definition), 3);
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        registry.unregister(location);
    }

    void onPlacedInteract(BlockDefinition definition, IgnisLocation location, IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction, IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        registry.openBlock(player, location);
        var gui = registry.blockGui(location);
        if (gui == null) {
            return;
        }
        IgnisItem display = gui.inventory().getItem(DISPLAY_SLOT);
        if (display != null && !display.isAir()) {
            player.sendMessage("<gray>Museum exhibit: <white>" + display.getAmount() + "x "
                    + display.getMaterialKey() + "</white></gray>");
            IgnisWorld world = worldAt(location);
            TheatricsSupport.sparkle(world, Locations.toCenter(location), "END_ROD", 6);
        }
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Display Case") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
