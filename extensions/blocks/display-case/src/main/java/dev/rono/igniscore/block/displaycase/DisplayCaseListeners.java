package dev.rono.igniscore.block.displaycase;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
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

final class DisplayCaseListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private static final int DISPLAY_SLOT = 13;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    DisplayCaseListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "display-case");
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Display Case") : definition.getTitle();
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
                IgnisItem display = gui.inventory().getItem(DISPLAY_SLOT);
                if (display != null && !display.isAir()) {
                    event.player().sendMessage("<gray>Museum exhibit: <white>" + display.getAmount() + "x "
                            + display.getMaterialKey() + "</white></gray>");
                    IgnisWorld world = worldAt(event.block().location());
                    TheatricsSupport.sparkle(world, Locations.toCenter(event.block().location()), "END_ROD", 6);
                }
    }
}
