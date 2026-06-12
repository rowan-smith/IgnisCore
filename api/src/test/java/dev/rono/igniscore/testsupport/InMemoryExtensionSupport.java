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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryExtensionSupport implements ExtensionSupport {
    private final Map<IgnisLocation, IgnisDropCollector> collectors = new ConcurrentHashMap<>();
    private final Map<Object, IgnisCustomInventory> customInventories = new ConcurrentHashMap<>();

    @Override
    public void registerDropCollector(IgnisLocation location, IgnisDropCollector collector) {
        collectors.put(location, collector);
    }

    @Override
    public void unregisterDropCollector(IgnisLocation location) {
        collectors.remove(location);
    }

    @Override
    public void registerCustomInventory(Object nativeInventory, IgnisCustomInventory handler) {
        customInventories.put(nativeInventory, handler);
    }

    @Override
    public void unregisterCustomInventory(Object nativeInventory) {
        customInventories.remove(nativeInventory);
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

    public boolean tryCollect(IgnisLocation breakLocation, Collection<IgnisItem> drops) {
        boolean collectedAny = false;
        for (IgnisDropCollector collector : collectors.values()) {
            if (collector.tryCollect(breakLocation, drops)) {
                collectedAny = true;
            }
        }
        drops.removeIf(stack -> stack == null || stack.getAmount() <= 0);
        return collectedAny;
    }
}
