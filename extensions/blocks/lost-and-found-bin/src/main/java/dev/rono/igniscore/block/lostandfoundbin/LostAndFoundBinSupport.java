package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class LostAndFoundBinSupport {
    private LostAndFoundBinSupport() {
    }

    static void tick(LostAndFoundBinRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        runtime.sweepCounter++;
        int interval = StrategySupport.customInt(definition, "sweepIntervalTicks", 72000);
        if (runtime.sweepCounter < interval / StrategySupport.customInt(definition, "tickPeriod", 100)) {
            return;
        }
        runtime.sweepCounter = 0;
        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        IgnisWorld world = worldAt(runtime, location);
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
            if (storeInBin(runtime, gui, dropped)) {
                world.removeEntity(entity);
                collected++;
            }
        }
        if (collected > 0) {
            world.playSound(center, "ENTITY_ITEM_PICKUP", 0.6f, 1.0f);
        }
    
    }

    static boolean storeInBin(LostAndFoundBinRuntime runtime, dev.rono.extensions.shared.gui.BlockStorageGui gui, IgnisItem stack) {

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

    static Component title(LostAndFoundBinRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Lost & Found") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(LostAndFoundBinRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

