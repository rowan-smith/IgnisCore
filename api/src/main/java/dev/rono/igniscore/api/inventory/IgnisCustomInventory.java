package dev.rono.igniscore.api.inventory;

import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Contract for custom block inventories opened by strategies.
 *
 * <p>Processing GUIs and chest-style UIs implement this interface to control which items may
 * enter the grid, which slots are decorative separators, and how the view is restored after
 * player interaction.</p>
 */
public interface IgnisCustomInventory {

    /**
     * @param stack item stack the player is attempting to place
     * @return {@code true} when the stack may be inserted into a content slot
     */
    boolean accepts(IgnisItem stack);

    /**
     * Re-applies decorative or locked items after the player moves content slots.
     */
    void restoreDecorations();

    /**
     * @param slot inventory index
     * @return {@code true} when the slot is a non-interactive separator or label row
     */
    boolean isSeparatorSlot(int slot);

    /**
     * Called when the viewing player closes the inventory.
     */
    default void onClose() {}

    /**
     * Called after any slot content change while the inventory remains open.
     */
    default void onChange() {}
}
