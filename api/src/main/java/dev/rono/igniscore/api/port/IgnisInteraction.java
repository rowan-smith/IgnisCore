package dev.rono.igniscore.api.port;

/**
 * How a player interacted with an item or block.
 *
 * <p>Delivered to item and block strategy callbacks so behavior can branch on
 * click type and target (air versus block).</p>
 */
public enum IgnisInteraction {

    /** Right-click while not targeting a block. */
    RIGHT_CLICK_AIR,

    /** Right-click on a block face. */
    RIGHT_CLICK_BLOCK,

    /** Left-click (attack) while not targeting a block. */
    LEFT_CLICK_AIR,

    /** Left-click (attack) on a block face. */
    LEFT_CLICK_BLOCK,

    /** Physical contact (for example stepping on a pressure plate). */
    PHYSICAL
}
