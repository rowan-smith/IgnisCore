package dev.rono.igniscore.api.port;

/**
 * Platform-neutral item stack handle exposed to extension strategies.
 */
public interface IgnisItem {

    int getAmount();

    void setAmount(int amount);

    String getMaterialKey();

    boolean isAir();

    /**
     * @return opaque platform item (e.g. Bukkit ItemStack)
     */
    Object nativeItem();
}
