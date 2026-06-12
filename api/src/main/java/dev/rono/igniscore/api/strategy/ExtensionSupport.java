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
 */
public interface ExtensionSupport {

    void registerDropCollector(IgnisLocation location, IgnisDropCollector collector);

    void unregisterDropCollector(IgnisLocation location);

    void registerCustomInventory(Object nativeInventory, IgnisCustomInventory handler);

    void unregisterCustomInventory(Object nativeInventory);

    IgnisWorld resolveWorld(IgnisLocation location);

    IgnisInventory createInventory(Object holder, int size, Component title);

    IgnisItem createItem(String materialKey, int amount);

    void openInventory(IgnisPlayer player, IgnisInventory inventory);

    IgnisPlayer wrapPlayer(Object nativeObject);

    Path getDataDirectory();
}
