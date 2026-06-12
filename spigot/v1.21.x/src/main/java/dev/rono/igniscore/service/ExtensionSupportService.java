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
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
        Location bukkitLocation = BukkitBridge.toBukkit(location);
        return platformAdapter.wrapWorld(bukkitLocation.getWorld());
    }

    @Override
    public IgnisInventory createInventory(Object holder, int size, Component title) {
        return platformAdapter.createInventory(holder, size, title);
    }

    @Override
    public IgnisItem createItem(String materialKey, int amount) {
        Material material = Material.matchMaterial(materialKey);
        if (material == null) {
            material = Material.STONE;
        }
        return BukkitBridge.wrap(new ItemStack(material, amount));
    }

    @Override
    public void openInventory(IgnisPlayer player, IgnisInventory inventory) {
        player.openInventory(inventory.nativeInventory());
    }

    @Override
    public IgnisPlayer wrapPlayer(Object nativeObject) {
        if (nativeObject instanceof Player player) {
            return BukkitBridge.wrap(player);
        }
        if (nativeObject instanceof IgnisPlayer ignisPlayer) {
            return ignisPlayer;
        }
        return null;
    }

    @Override
    public Path getDataDirectory() {
        return platformAdapter.getDataDirectory();
    }

    public IgnisCustomInventory getCustomInventory(Object nativeInventory) {
        return customInventories.get(nativeInventory);
    }

    public boolean tryCollect(Location breakLocation, Collection<ItemStack> drops) {
        IgnisLocation ignisLocation = blockLocation(BukkitBridge.toIgnis(breakLocation));
        List<IgnisItem> ignisDrops = new ArrayList<>();
        for (ItemStack stack : drops) {
            if (stack != null && !stack.getType().isAir()) {
                ignisDrops.add(BukkitBridge.wrap(stack));
            }
        }

        boolean collectedAny = false;
        for (IgnisDropCollector collector : collectors.values()) {
            if (collector.tryCollect(ignisLocation, ignisDrops)) {
                collectedAny = true;
            }
        }

        drops.clear();
        for (IgnisItem item : ignisDrops) {
            ItemStack stack = BukkitBridge.unwrap(item);
            if (stack != null && !stack.getType().isAir() && stack.getAmount() > 0) {
                drops.add(stack);
            }
        }
        return collectedAny;
    }

    public void clear() {
        collectors.clear();
        customInventories.clear();
    }

    private static IgnisLocation blockLocation(IgnisLocation location) {
        return new IgnisLocation(
                location.worldId(),
                location.worldName(),
                Math.floor(location.x()),
                Math.floor(location.y()),
                Math.floor(location.z()),
                location.yaw(),
                location.pitch());
    }
}
