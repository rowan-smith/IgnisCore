package dev.rono.igniscore.api.port;

import java.util.UUID;

/**
 * Platform-neutral player handle exposed to extension strategies.
 *
 * <p>Provides identity, position, messaging, and inventory UI access without
 * exposing native player types.</p>
 */
public interface IgnisPlayer {

    /**
     * @return stable unique id for this player
     */
    UUID getUniqueId();

    /**
     * @return current display name
     */
    String getName();

    /**
     * @return feet position in the player's current world
     */
    IgnisLocation getLocation();

    /**
     * @return eye position (used for raycasts and line-of-sight checks)
     */
    IgnisLocation getEyeLocation();

    /**
     * @return the world this player is currently in
     */
    IgnisWorld getWorld();

    /**
     * Sends a chat message formatted with MiniMessage.
     *
     * @param miniMessage MiniMessage-formatted text
     */
    void sendMessage(String miniMessage);

    /**
     * Opens a native inventory UI for this player.
     *
     * @param nativeInventory opaque platform inventory (for example Bukkit {@code Inventory})
     */
    void openInventory(Object nativeInventory);
}
