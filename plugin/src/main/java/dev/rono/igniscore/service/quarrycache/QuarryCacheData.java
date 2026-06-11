package dev.rono.igniscore.service.quarrycache;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

final class QuarryCacheData {
    final Location location;
    final double collectRadius;
    final QuarryCacheInventory inventory;

    QuarryCacheData(Location location, double collectRadius, QuarryCacheInventory inventory) {
        this.location = location.getBlock().getLocation();
        this.collectRadius = collectRadius;
        this.inventory = inventory;
    }

    boolean isWithinRadius(Location target) {
        if (!location.getWorld().equals(target.getWorld())) {
            return false;
        }
        return location.distance(target.getBlock().getLocation()) <= collectRadius;
    }

    ItemStack[] collectFilterItems() {
        return inventory.getFilterItems();
    }
}
