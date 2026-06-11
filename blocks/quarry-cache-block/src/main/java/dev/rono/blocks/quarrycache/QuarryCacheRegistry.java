package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategySupport;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class QuarryCacheRegistry {
    private final Map<Location, QuarryCacheData> caches = new ConcurrentHashMap<>();

    void register(Location location, BlockDefinition definition) {
        Location blockLocation = location.getBlock().getLocation();
        double radius = resolveCollectRadius(definition);
        Component title = definition.getTitle() == null ? Component.text("Quarry Cache") : definition.getTitle();
        QuarryCacheInventory inventory = new QuarryCacheInventory(blockLocation, title);
        caches.put(blockLocation, new QuarryCacheData(blockLocation, radius, inventory));
    }

    void unregister(Location location) {
        caches.remove(location.getBlock().getLocation());
    }

    boolean isCache(Location location) {
        return caches.containsKey(location.getBlock().getLocation());
    }

    void openGui(Player player, Location location) {
        QuarryCacheData cache = caches.get(location.getBlock().getLocation());
        if (cache == null) {
            return;
        }
        cache.inventory.restoreSeparators();
        player.openInventory(cache.inventory.getInventory());
    }

    void dropContents(Location location) {
        QuarryCacheData cache = caches.remove(location.getBlock().getLocation());
        if (cache == null) {
            return;
        }

        Location dropLocation = cache.location.clone().add(0.5, 0.5, 0.5);
        Inventory inventory = cache.inventory.getInventory();
        for (int slot = 0; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            if (QuarryCacheInventory.isSeparatorSlot(slot)) {
                continue;
            }
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.getType().isAir()) {
                continue;
            }
            dropLocation.getWorld().dropItemNaturally(dropLocation, item.clone());
        }
    }

    boolean tryCollect(Location breakLocation, Collection<ItemStack> drops) {
        QuarryCacheData cache = findCollectingCache(breakLocation);
        if (cache == null) {
            return false;
        }
        return tryStore(cache, drops);
    }

    void cleanup() {
        for (Location location : new HashMap<>(caches).keySet()) {
            dropContents(location);
        }
        caches.clear();
    }

    private QuarryCacheData findCollectingCache(Location breakLocation) {
        QuarryCacheData nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        Location normalized = breakLocation.getBlock().getLocation();

        for (QuarryCacheData cache : caches.values()) {
            if (!cache.isWithinRadius(normalized)) {
                continue;
            }
            double distance = cache.location.distance(normalized);
            if (distance < nearestDistance) {
                nearest = cache;
                nearestDistance = distance;
            }
        }
        return nearest;
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
