package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
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
import java.util.Collection;
import net.kyori.adventure.text.Component;

final class LostAndFoundBinListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;
    private int sweepCounter;

    LostAndFoundBinListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "lost-and-found-bin");
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        sweepCounter++;
        int interval = StrategySupport.customInt(definition, "sweepIntervalTicks", 72000);
        if (sweepCounter < interval / StrategySupport.customInt(definition, "tickPeriod", 100)) {
            return;
        }
        sweepCounter = 0;
        var gui = registry.blockGui(location);
        if (gui == null) {
            return;
        }
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        double radius = StrategySupport.customDouble(definition, "collectRadius", 8.0);
        int collected = 0;
        for (Object entity : world.getNearbyEntities(center, radius)) {
            if (!EntityUtilSupport.isLootEntity(entity)) {
                continue;
            }
            IgnisItem dropped = world.getDroppedItem(entity);
            if (dropped == null || dropped.isAir()) {
                continue;
            }
            if (storeInBin(gui, dropped)) {
                world.removeEntity(entity);
                collected++;
            }
        }
        if (collected > 0) {
            world.playSound(center, "ENTITY_ITEM_PICKUP", 0.6f, 1.0f);
        }
    }

    private boolean storeInBin(dev.rono.extensions.shared.gui.BlockStorageGui gui, IgnisItem stack) {
        var inventory = gui.inventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            IgnisItem existing = inventory.getItem(slot);
            if (existing == null || existing.isAir()) {
                inventory.setItem(slot, stack);
                return true;
            }
            if (existing.getMaterialKey().equals(stack.getMaterialKey()) && existing.getAmount() < 64) {
                int move = Math.min(stack.getAmount(), 64 - existing.getAmount());
                existing.setAmount(existing.getAmount() + move);
                stack.setAmount(stack.getAmount() - move);
                inventory.setItem(slot, existing);
                return stack.getAmount() <= 0;
            }
        }
        return false;
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Lost & Found") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.registerBlock(event.block().location(), title(event.block().definition()), 6);
                PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 100),
                        () -> tick(event.block().definition(), event.block().location()));
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
                registry.unregister(event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() == CustomBlockAction.OPEN) {
                    registry.openBlock(event.player(), event.block().location());
                }
    }
}
