package dev.rono.igniscore.api.port;

import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;
import java.util.logging.Logger;

/**
 * Aggregated platform port surface implemented by each version-specific adapter module.
 */
public interface PlatformAdapter {

    PlatformType getPlatformType();

    String getMinecraftVersion();

    Logger getLogger();

    Path getDataDirectory();

    IgnisScheduler getScheduler();

    IgnisItem wrapItem(Object nativeItem);

    IgnisPlayer wrapPlayer(Object nativePlayer);

    IgnisBlock wrapBlock(Object nativeBlock);

    IgnisWorld wrapWorld(Object nativeWorld);

    IgnisLocation unwrapLocation(Object nativeLocation);

    Object nativeLocation(IgnisLocation location);

    void applyCustomModelData(Object nativeItem, int modelData);

    OptionalInt readCustomModelData(Object nativeItem);

    void applyItemMeta(Object nativeItem, Component displayName, List<Component> lore, String itemModelKey);

    void sendResourcePack(Object nativePlayer, String url, byte[] hash, boolean force);

    void sendMessage(Object nativeSender, Component message);

    boolean isBlockReplaceable(Object nativeBlock);

    String resolveSoundKey(String bukkitStyleSoundName);

    IgnisInventory createInventory(Object holder, int size, Component title);

    void registerEventListeners(Object listenerRegistry);

    void registerCommand(String name, Object commandExecutor);

    IgnisWorld resolveWorld(IgnisLocation location);

    IgnisItem createMaterialItem(String materialKey, int amount);

    void clearBlock(IgnisLocation location);

    void shutdown();

    default void spectateEntity(IgnisPlayer player, Object platformEntity, int durationTicks) {
    }
}
