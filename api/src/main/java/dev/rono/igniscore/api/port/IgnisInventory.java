package dev.rono.igniscore.api.port;

/**
 * Platform-neutral inventory handle for custom extension UIs.
 *
 * <p>Slots are zero-based. Items are exchanged as {@link IgnisItem} handles
 * so strategies never touch native inventory APIs directly.</p>
 */
public interface IgnisInventory {

    /**
     * @return total number of slots in this inventory
     */
    int getSize();

    /**
     * Places an item in the given slot, replacing any existing stack.
     *
     * @param slot zero-based slot index
     * @param item item to place, or air to clear the slot
     */
    void setItem(int slot, IgnisItem item);

    /**
     * @param slot zero-based slot index
     * @return item in the slot, or an air handle when empty
     */
    IgnisItem getItem(int slot);

    /**
     * @return opaque platform inventory (for example Bukkit {@code Inventory})
     */
    Object nativeInventory();
}
