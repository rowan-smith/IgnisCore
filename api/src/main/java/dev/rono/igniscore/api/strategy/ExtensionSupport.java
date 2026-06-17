package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;

/**
 * Hooks that let block/item strategies register behavior with the core runtime
 * without registering platform listeners from extension classloaders.
 *
 * <p>Access via {@link IgnisStrategyContext#extensions()}. The core registers listeners on the
 * platform side and forwards events into extension code through these APIs.</p>
 */
public interface ExtensionSupport {

    /**
     * Registers a drop collector for a placed custom block location.
     *
     * <p>Collectors receive item drops produced when the block breaks or is mined.</p>
     *
     * @param location world position of the custom block
     * @param collector callback that may modify or suppress drops
     */
    void registerDropCollector(IgnisLocation location, IgnisDropCollector collector);

    /**
     * Removes any drop collector registered for the given location.
     *
     * @param location world position previously passed to {@link #registerDropCollector}
     */
    void unregisterDropCollector(IgnisLocation location);

    /**
     * Binds a custom inventory handler to a native platform inventory instance.
     *
     * @param nativeInventory platform-specific inventory object
     * @param handler Ignis inventory callbacks
     */
    void registerCustomInventory(Object nativeInventory, IgnisCustomInventory handler);

    /**
     * Removes the custom handler for a native inventory.
     *
     * @param nativeInventory platform-specific inventory object
     */
    void unregisterCustomInventory(Object nativeInventory);

    /**
     * Resolves the world containing the given location.
     *
     * @param location position within a world
     * @return platform-neutral world handle
     */
    IgnisWorld resolveWorld(IgnisLocation location);

    /**
     * Creates a platform inventory with the given size and title.
     *
     * @param holder optional inventory holder, or {@code null}
     * @param size slot count (must be valid for the platform)
     * @param title display title shown to players
     * @return platform-neutral inventory handle
     */
    IgnisInventory createInventory(Object holder, int size, Component title);

    /**
     * Creates a vanilla or mapped material item stack.
     *
     * @param materialKey platform material identifier (for example {@code DIAMOND})
     * @param amount stack size
     * @return platform-neutral item handle
     */
    IgnisItem createItem(String materialKey, int amount);

    /**
     * Opens an inventory for the given player.
     *
     * @param player viewer
     * @param inventory inventory created via {@link #createInventory}
     */
    void openInventory(IgnisPlayer player, IgnisInventory inventory);

    /**
     * Wraps a native platform player object as an {@link IgnisPlayer}.
     *
     * @param nativeObject platform-specific player instance
     * @return platform-neutral player handle
     */
    IgnisPlayer wrapPlayer(Object nativeObject);

    /**
     * Returns the core plugin data directory on the server filesystem.
     *
     * @return path suitable for extension persistence
     */
    Path getDataDirectory();
}
