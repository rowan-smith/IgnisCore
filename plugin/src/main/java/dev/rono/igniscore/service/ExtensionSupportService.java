package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ExtensionSupportService implements ExtensionSupport {
    private final Map<Location, IgnisDropCollector> collectors = new ConcurrentHashMap<>();
    private final Map<Inventory, IgnisCustomInventory> customInventories = new ConcurrentHashMap<>();

    @Inject
    public ExtensionSupportService() {
    }

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

    public IgnisCustomInventory getCustomInventory(Inventory inventory) {
        return customInventories.get(inventory);
    }

    public boolean tryCollect(Location breakLocation, Collection<ItemStack> drops) {
        for (IgnisDropCollector collector : collectors.values()) {
            if (collector.tryCollect(breakLocation, drops)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        collectors.clear();
        customInventories.clear();
    }
}
