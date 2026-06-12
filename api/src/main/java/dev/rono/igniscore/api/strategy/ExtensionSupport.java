package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

/**
 * Hooks that let block/item strategies register behavior with the core plugin
 * without registering Bukkit listeners from extension classloaders.
 */
public interface ExtensionSupport {

    void registerDropCollector(Location location, IgnisDropCollector collector);

    void unregisterDropCollector(Location location);

    void registerCustomInventory(Inventory inventory, IgnisCustomInventory handler);

    void unregisterCustomInventory(Inventory inventory);
}
