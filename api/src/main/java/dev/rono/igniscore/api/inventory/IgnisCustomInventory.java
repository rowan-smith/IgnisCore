package dev.rono.igniscore.api.inventory;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Marker for custom block inventories opened by strategies.
 * Implemented by extension modules; handled by the core plugin listener.
 */
public interface IgnisCustomInventory extends InventoryHolder {

    boolean accepts(ItemStack stack);

    void restoreDecorations();

    boolean isSeparatorSlot(int slot);

    default void onClose() {}

    default void onChange() {}
}
