package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ExtensionSupportService implements ExtensionSupport {
    private final PlatformAdapter platformAdapter;
    private final Map<IgnisLocation, IgnisDropCollector> collectors = new ConcurrentHashMap<>();
    private final Map<Object, IgnisCustomInventory> customInventories = new ConcurrentHashMap<>();

    @Inject
    public ExtensionSupportService(PlatformAdapter platformAdapter) {
        this.platformAdapter = platformAdapter;
    }

    @Override
    public void registerDropCollector(IgnisLocation location, IgnisDropCollector collector) {
        collectors.put(Locations.toBlock(location), collector);
    }

    @Override
    public void unregisterDropCollector(IgnisLocation location) {
        collectors.remove(Locations.toBlock(location));
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
        return platformAdapter.resolveWorld(location);
    }

    @Override
    public IgnisInventory createInventory(Object holder, int size, Component title) {
        return platformAdapter.createInventory(holder, size, title);
    }

    @Override
    public IgnisItem createItem(String materialKey, int amount) {
        return platformAdapter.createMaterialItem(materialKey, amount);
    }

    @Override
    public void openInventory(IgnisPlayer player, IgnisInventory inventory) {
        player.openInventory(inventory.nativeInventory());
    }

    @Override
    public IgnisPlayer wrapPlayer(Object nativeObject) {
        return platformAdapter.wrapPlayer(nativeObject);
    }

    @Override
    public Path getDataDirectory() {
        return platformAdapter.getDataDirectory();
    }

    public IgnisCustomInventory getCustomInventory(Object nativeInventory) {
        return customInventories.get(nativeInventory);
    }

    public boolean tryCollect(IgnisLocation breakLocation, Collection<IgnisItem> drops) {
        IgnisLocation ignisLocation = Locations.toBlock(breakLocation);
        List<IgnisItem> ignisDrops = new ArrayList<>(drops);

        boolean collectedAny = false;
        for (IgnisDropCollector collector : collectors.values()) {
            if (collector.tryCollect(ignisLocation, ignisDrops)) {
                collectedAny = true;
            }
        }

        drops.clear();
        drops.addAll(ignisDrops.stream()
                .filter(item -> item != null && item.getAmount() > 0)
                .toList());
        return collectedAny;
    }

    public void clear() {
        collectors.clear();
        customInventories.clear();
    }
}
