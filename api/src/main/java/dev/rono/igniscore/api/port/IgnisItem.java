package dev.rono.igniscore.api.port;

/**
 * Platform-neutral item stack handle exposed to extension strategies.
 *
 * <p>Represents a single stack with amount and material identity. Use
 * {@link #nativeItem()} only when bridging to platform-specific APIs.</p>
 */
public interface IgnisItem {

    /**
     * @return stack size (1–64 for most materials)
     */
    int getAmount();

    /**
     * Sets the stack size.
     *
     * @param amount new stack size
     */
    void setAmount(int amount);

    /**
     * @return material key for this stack (for example {@code minecraft:diamond})
     */
    String getMaterialKey();

    /**
     * @return whether this stack represents empty air
     */
    boolean isAir();

    /**
     * @return opaque platform item (for example Bukkit {@code ItemStack})
     */
    Object nativeItem();
}
