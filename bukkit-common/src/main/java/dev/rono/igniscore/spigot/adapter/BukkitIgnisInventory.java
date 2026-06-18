package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import org.bukkit.inventory.Inventory;

public final class BukkitIgnisInventory implements IgnisInventory {
    private final Inventory handle;

    public BukkitIgnisInventory(Inventory handle) {
        this.handle = handle;
    }

    public Inventory getHandle() {
        return handle;
    }

    @Override
    public int getSize() {
        return handle.getSize();
    }

    @Override
    public void setItem(int slot, IgnisItem item) {
        handle.setItem(slot, BukkitBridge.unwrap(item));
    }

    @Override
    public IgnisItem getItem(int slot) {
        return BukkitBridge.wrap(handle.getItem(slot));
    }

    @Override
    public Object nativeInventory() {
        return handle;
    }
}
