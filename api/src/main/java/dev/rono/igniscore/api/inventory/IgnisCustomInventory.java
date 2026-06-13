package dev.rono.igniscore.api.inventory;

import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Marker for custom block inventories opened by strategies.
 */
public interface IgnisCustomInventory {

    boolean accepts(IgnisItem stack);

    void restoreDecorations();

    boolean isSeparatorSlot(int slot);

    default void onClose() {}

    default void onChange() {}
}
