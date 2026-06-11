package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.StrategySupport;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class QuarryCacheRegistry {
    private final ExtensionSupport extensionSupport;
    private final Map<Location, QuarryCacheData> caches = new ConcurrentHashMap<>();

    QuarryCacheRegistry(ExtensionSupport extensionSupport) {
        this.extensionSupport = extensionSupport;
    }

    void register(Location location, BlockDefinition definition) {
        Location blockLocation = location.getBlock().getLocation();
        double radius = resolveCollectRadius(definition);
        Component title = definition.getTitle() == null ? Component.text("Quarry Cache") : definition.getTitle();
        QuarryCacheInventory inventory = new QuarryCacheInventory(blockLocation, title);
        QuarryCacheData cache = new QuarryCacheData(blockLocation, radius, inventory);
        caches.put(blockLocation, cache);
        extensionSupport.registerDropCollector(blockLocation, (breakLocation, drops) -> tryCollect(cache, breakLocation, drops));
    }

    void unregister(Location location) {
        Location blockLocation = location.getBlock().getLocation();
        caches.remove(blockLocation);
        extensionSupport.unregisterDropCollector(blockLocation);
    }

    void openGui(Player player, Location location) {
        QuarryCacheData cache = caches.get(location.getBlock().getLocation());
        if (cache == null) {
            return;
        }
        cache.inventory.restoreDecorations();
        player.openInventory(cache.inventory.getInventory());
    }

    void dropContents(Location location) {
        QuarryCacheData cache = caches.remove(location.getBlock().getLocation());
        extensionSupport.unregisterDropCollector(location.getBlock().getLocation());
        if (cache == null) {
            return;
        }

        Location dropLocation = cache.location.clone().add(0.5, 0.5, 0.5);
        Inventory inventory = cache.inventory.getInventory();
        for (int slot = 0; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            if (cache.inventory.isSeparatorSlot(slot)) {
                continue;
            }
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            dropLocation.getWorld().dropItemNaturally(dropLocation, item.clone());
        }
    }

    private boolean tryCollect(QuarryCacheData cache, Location breakLocation, Collection<ItemStack> drops) {
        if (!cache.isWithinRadius(breakLocation)) {
            return false;
        }
        return tryStore(cache, drops);
    }

    private boolean tryStore(QuarryCacheData cache, Collection<ItemStack> drops) {
        boolean storedAny = false;
        Inventory inventory = cache.inventory.getInventory();

        for (ItemStack drop : drops) {
            if (drop == null || drop.getType().isAir()) {
                continue;
            }
            if (!cache.inventory.accepts(drop)) {
                continue;
            }

            ItemStack remaining = storeInStorage(inventory, drop.clone());
            if (remaining == null || remaining.getAmount() <= 0) {
                storedAny = true;
            } else if (remaining.getAmount() < drop.getAmount()) {
                storedAny = true;
            }
        }
        return storedAny;
    }

    private ItemStack storeInStorage(Inventory inventory, ItemStack stack) {
        for (int slot = QuarryCacheInventory.STORAGE_START; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            ItemStack existing = inventory.getItem(slot);
            if (existing == null || existing.getType().isAir()) {
                inventory.setItem(slot, stack);
                return null;
            }
            if (existing.isSimilar(stack) && existing.getAmount() < existing.getMaxStackSize()) {
                int transferable = Math.min(stack.getAmount(), existing.getMaxStackSize() - existing.getAmount());
                existing.setAmount(existing.getAmount() + transferable);
                stack.setAmount(stack.getAmount() - transferable);
                if (stack.getAmount() <= 0) {
                    return null;
                }
            }
        }

        for (int slot = QuarryCacheInventory.STORAGE_START; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            ItemStack existing = inventory.getItem(slot);
            if (existing == null || existing.getType().isAir()) {
                inventory.setItem(slot, stack);
                return null;
            }
        }
        return stack;
    }

    private double resolveCollectRadius(BlockDefinition definition) {
        Map<String, Object> customData = definition.getCustomData();
        if (customData.containsKey("collectRadius")) {
            return StrategySupport.customDouble(customData, "collectRadius", 5.0);
        }
        return StrategySupport.customDouble(customData, "collect_radius", 5.0);
    }
}
