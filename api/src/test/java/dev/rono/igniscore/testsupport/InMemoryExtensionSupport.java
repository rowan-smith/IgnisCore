package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryExtensionSupport implements ExtensionSupport {
    private final Map<Location, IgnisDropCollector> collectors = new ConcurrentHashMap<>();
    private final Map<Inventory, IgnisCustomInventory> customInventories = new ConcurrentHashMap<>();

    @Override
    public void registerDropCollector(Location location, IgnisDropCollector collector) {
        collectors.put(location.getBlock().getLocation(), collector);
    }

    @Override
    public void unregisterDropCollector(Location location) {
        collectors.remove(location.getBlock().getLocation());
    }

    @Override
    public void registerCustomInventory(Inventory inventory, IgnisCustomInventory handler) {
        customInventories.put(inventory, handler);
    }

    @Override
    public void unregisterCustomInventory(Inventory inventory) {
        customInventories.remove(inventory);
    }

    public boolean tryCollect(Location breakLocation, Collection<ItemStack> drops) {
        boolean collectedAny = false;
        for (IgnisDropCollector collector : collectors.values()) {
            if (collector.tryCollect(breakLocation, drops)) {
                collectedAny = true;
            }
        }
        drops.removeIf(stack -> stack == null || stack.getType().isAir() || stack.getAmount() <= 0);
        return collectedAny;
    }
}
