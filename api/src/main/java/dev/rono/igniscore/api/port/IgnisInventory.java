package dev.rono.igniscore.api.port;

/**
 * Platform-neutral inventory handle for custom extension UIs.
 */
public interface IgnisInventory {

    int getSize();

    void setItem(int slot, IgnisItem item);

    IgnisItem getItem(int slot);

    /**
     * @return opaque platform inventory (e.g. Bukkit Inventory)
     */
    Object nativeInventory();
}
