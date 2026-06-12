package dev.rono.igniscore.sponge.service;

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
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;
import net.kyori.adventure.text.Component;
import dev.rono.igniscore.sponge.support.SpongeRegistrySupport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.ItemStack;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SpongeExtensionSupportService implements ExtensionSupport {
    private final PlatformAdapter platformAdapter;
    private final Map<IgnisLocation, IgnisDropCollector> collectors = new ConcurrentHashMap<>();
    private final Map<Object, IgnisCustomInventory> customInventories = new ConcurrentHashMap<>();

    @Inject
    public SpongeExtensionSupportService(PlatformAdapter platformAdapter) {
        this.platformAdapter = platformAdapter;
    }

    @Override
    public void registerDropCollector(IgnisLocation location, IgnisDropCollector collector) {
        collectors.put(blockLocation(location), collector);
    }

    @Override
    public void unregisterDropCollector(IgnisLocation location) {
        collectors.remove(blockLocation(location));
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
        var defaultWorld = SpongeRuntimeHolder.server().worldManager().worlds().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No worlds loaded"));
        return SpongeBridge.wrap(SpongeBridge.resolveWorld(location, defaultWorld));
    }

    @Override
    public IgnisInventory createInventory(Object holder, int size, Component title) {
        return platformAdapter.createInventory(holder, size, title);
    }

    @Override
    public IgnisItem createItem(String materialKey, int amount) {
        ResourceKey key = ResourceKey.resolve(materialKey.toLowerCase(Locale.ROOT));
        var itemType = SpongeRegistrySupport.findItemType(key)
                .orElseThrow(() -> new IllegalArgumentException("Unknown item type: " + materialKey));
        return SpongeBridge.wrap(ItemStack.of(itemType, amount));
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

    private static IgnisLocation blockLocation(IgnisLocation location) {
        return new IgnisLocation(location.worldName(),
                Math.floor(location.x()),
                Math.floor(location.y()),
                Math.floor(location.z()));
    }
}
