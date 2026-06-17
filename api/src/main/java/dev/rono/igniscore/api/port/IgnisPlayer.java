package dev.rono.igniscore.api.port;

import java.util.UUID;

/**
 * Platform-neutral player handle exposed to extension strategies.
 */
public interface IgnisPlayer {

    UUID getUniqueId();

    String getName();

    IgnisLocation getLocation();

    IgnisLocation getEyeLocation();

    IgnisWorld getWorld();

    void sendMessage(String miniMessage);

    void openInventory(Object nativeInventory);

    /**
     * Applies a potion effect using Bukkit/Sponge effect enum names (e.g. BLINDNESS, NAUSEA).
     */
    void applyPotionEffect(String effectKey, int durationTicks, int amplifier);
}
