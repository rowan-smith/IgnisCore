package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import java.util.Collection;
import net.kyori.adventure.text.Component;

final class ChickenCoopCacheSupport {
    private ChickenCoopCacheSupport() {
    }

    static boolean collectEggs(ChickenCoopCacheRuntime runtime, IgnisLocation coopLocation, Collection<IgnisItem> drops) {

        var gui = runtime.registry.blockGui(coopLocation);
        if (gui == null) {
            return false;
        }
        boolean stored = false;
        var inventory = gui.inventory();
        var iterator = drops.iterator();
        while (iterator.hasNext()) {
            IgnisItem drop = iterator.next();
            if (drop == null || drop.isAir() || !isEgg(runtime, drop)) {
                continue;
            }
            if (storeItem(runtime, inventory, drop)) {
                iterator.remove();
                stored = true;
            }
        }
        return stored;
    
    }

    static boolean storeItem(ChickenCoopCacheRuntime runtime, dev.rono.igniscore.api.port.IgnisInventory inventory, IgnisItem stack) {

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

    static void tick(ChickenCoopCacheRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        for (Object entity : world.getNearbyEntities(center, StrategySupport.customDouble(definition, "coopRadius", 6.0))) {
            if (entity.getClass().getSimpleName().toLowerCase().contains("chicken")) {
                TheatricsSupport.sparkle(world, center, "EGG_CRACK", 2);
                world.playSound(center, "ENTITY_CHICKEN_EGG", 0.3f, 1.2f);
                var gui = runtime.registry.blockGui(location);
                if (gui != null) {
                    storeItem(runtime, gui.inventory(), runtime.context.extensions().createItem("egg", 1));
                }
                break;
            }
        }
    
    }

    static boolean isEgg(ChickenCoopCacheRuntime runtime, IgnisItem item) {

        return "egg".equalsIgnoreCase(item.getMaterialKey());
    
    }

    static Component title(ChickenCoopCacheRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Chicken Coop") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(ChickenCoopCacheRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

