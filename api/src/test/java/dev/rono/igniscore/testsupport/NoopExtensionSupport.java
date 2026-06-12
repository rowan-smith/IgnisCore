package dev.rono.igniscore.testsupport;

import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;

public final class NoopExtensionSupport implements ExtensionSupport {
    public static final ExtensionSupport INSTANCE = new NoopExtensionSupport();

    private NoopExtensionSupport() {
    }

    @Override
    public void registerDropCollector(IgnisLocation location, IgnisDropCollector collector) {
    }

    @Override
    public void unregisterDropCollector(IgnisLocation location) {
    }

    @Override
    public void registerCustomInventory(Object nativeInventory, IgnisCustomInventory handler) {
    }

    @Override
    public void unregisterCustomInventory(Object nativeInventory) {
    }

    @Override
    public IgnisWorld resolveWorld(IgnisLocation location) {
        return null;
    }

    @Override
    public IgnisInventory createInventory(Object holder, int size, Component title) {
        return null;
    }

    @Override
    public IgnisItem createItem(String materialKey, int amount) {
        return null;
    }

    @Override
    public void openInventory(IgnisPlayer player, IgnisInventory inventory) {
    }

    @Override
    public IgnisPlayer wrapPlayer(Object nativeObject) {
        return null;
    }

    @Override
    public Path getDataDirectory() {
        return null;
    }
}
