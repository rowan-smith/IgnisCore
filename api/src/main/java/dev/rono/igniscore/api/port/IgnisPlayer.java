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

    default void sendActionBar(String miniMessage) {
        sendMessage(miniMessage);
    }

    /**
     * Applies a potion-like effect using platform-native effect types (e.g. SLOWNESS, GLOWING).
     */
    default void applyPotionEffect(String effectKey, int durationTicks, int amplifier) {
    }

    void openInventory(Object nativeInventory);
}
