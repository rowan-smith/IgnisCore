package dev.rono.igniscore.api.port;

import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Logger;

/**
 * Aggregated platform port surface implemented by each version-specific adapter module.
 *
 * <p>Bridges native host APIs to Ignis port types ({@link IgnisPlayer},
 * {@link IgnisWorld}, {@link IgnisItem}, etc.) and exposes cross-cutting
 * utilities such as scheduling, inventory creation, and resource pack delivery.</p>
 */
public interface PlatformAdapter {

    /**
     * @return host platform family for this adapter
     */
    PlatformType getPlatformType();

    /**
     * @return Minecraft version string reported by the host (for example {@code 1.21.1})
     */
    String getMinecraftVersion();

    /**
     * @return logger scoped to the platform plugin or mod
     */
    Logger getLogger();

    /**
     * @return data directory for persistent plugin files
     */
    Path getDataDirectory();

    /**
     * @return task scheduler bound to this server instance
     */
    IgnisScheduler getScheduler();

    /**
     * Wraps a native item stack as an {@link IgnisItem}.
     *
     * @param nativeItem opaque platform item (for example Bukkit {@code ItemStack})
     * @return platform-neutral item handle
     */
    IgnisItem wrapItem(Object nativeItem);

    /**
     * Wraps a native player as an {@link IgnisPlayer}.
     *
     * @param nativePlayer opaque platform player
     * @return platform-neutral player handle
     */
    IgnisPlayer wrapPlayer(Object nativePlayer);

    /**
     * Wraps a native block as an {@link IgnisBlock}.
     *
     * @param nativeBlock opaque platform block
     * @return platform-neutral block handle
     */
    IgnisBlock wrapBlock(Object nativeBlock);

    /**
     * Wraps a native world as an {@link IgnisWorld}.
     *
     * @param nativeWorld opaque platform world
     * @return platform-neutral world handle
     */
    IgnisWorld wrapWorld(Object nativeWorld);

    /**
     * Converts a native location object to {@link IgnisLocation}.
     *
     * @param nativeLocation opaque platform location
     * @return platform-neutral location
     */
    IgnisLocation unwrapLocation(Object nativeLocation);

    /**
     * Converts an {@link IgnisLocation} to the native location type.
     *
     * @param location platform-neutral location
     * @return opaque platform location
     */
    Object nativeLocation(IgnisLocation location);

    /**
     * Applies custom model data to a native item for resource-pack models.
     *
     * @param nativeItem target item stack
     * @param modelData custom model data integer
     */
    void applyCustomModelData(Object nativeItem, int modelData);

    /**
     * Reads custom model data from a native item.
     *
     * @param nativeItem item stack to inspect
     * @return model data when present, otherwise empty
     */
    OptionalInt readCustomModelData(Object nativeItem);

    /**
     * Applies display name, lore, and item model key to a native item.
     *
     * @param nativeItem target item stack
     * @param displayName Adventure display name component
     * @param lore Adventure lore lines
     * @param itemModelKey namespaced item model key for 1.21+ item models
     */
    void applyItemMeta(Object nativeItem, Component displayName, List<Component> lore, String itemModelKey);

    /**
     * Sends a resource pack download prompt to a player.
     *
     * @param nativePlayer opaque platform player
     * @param url pack download URL
     * @param hash SHA-1 hash bytes for pack verification, or empty array
     * @param force whether to disconnect the player if the pack is declined
     */
    void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force);

    /**
     * Sends an Adventure component message to a command sender or player.
     *
     * @param nativeSender opaque platform sender
     * @param message message component
     */
    void sendMessage(Object nativeSender, Component message);

    /**
     * @param nativeBlock opaque platform block
     * @return whether the block can be replaced by another material
     */
    boolean isBlockReplaceable(Object nativeBlock);

    /**
     * Resolves a Bukkit-style sound enum name to a namespaced sound key.
     *
     * @param bukkitStyleSoundName legacy sound name (for example {@code ENTITY_GENERIC_EXPLODE})
     * @return namespaced key understood by {@link IgnisWorld#playSound}
     */
    String resolveSoundKey(String bukkitStyleSoundName);

    /**
     * Creates a platform inventory with a title.
     *
     * @param holder opaque inventory holder, or {@code null}
     * @param size slot count (must be a multiple of 9 on Bukkit-like hosts)
     * @param title Adventure inventory title
     * @return platform-neutral inventory handle
     */
    IgnisInventory createInventory(Object holder, int size, Component title);

    /**
     * Registers event listener objects with the host.
     *
     * @param listenerRegistry opaque listener registry or plugin instance
     */
    void registerEventListeners(Object listenerRegistry);

    /**
     * Registers a slash command with the host.
     *
     * @param name command label without slash
     * @param commandExecutor opaque command executor object
     */
    void registerCommand(String name, Object commandExecutor);

    /**
     * Resolves the world for a location using id or name.
     *
     * @param location location containing world id and/or name
     * @return wrapped world handle
     */
    IgnisWorld resolveWorld(IgnisLocation location);

    /**
     * Creates a vanilla material item stack.
     *
     * @param materialKey namespaced material key
     * @param amount stack size
     * @return platform-neutral item handle
     */
    IgnisItem createMaterialItem(String materialKey, int amount);

    /**
     * Clears the block at the given position (sets to air).
     *
     * @param location block position to clear
     */
    void clearBlock(IgnisLocation location);

    /**
     * Releases adapter resources during plugin or mod shutdown.
     */
    void shutdown();

    default void spectateEntity(IgnisPlayer player, Object platformEntity, int durationTicks) {
    }
}
