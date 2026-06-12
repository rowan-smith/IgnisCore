package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class QuarryCacheRegistry {
    private final Plugin plugin;
    private final ExtensionSupport extensionSupport;
    private final QuarryCacheStorage storage;
    private final Map<Location, QuarryCacheData> caches = new ConcurrentHashMap<>();
    private final Map<Location, BukkitTask> indicatorTasks = new ConcurrentHashMap<>();

    QuarryCacheRegistry(IgnisStrategyContext context) {
        this.plugin = context.getPlugin();
        this.extensionSupport = context.getExtensionSupport();
        this.storage = new QuarryCacheStorage(plugin, context.getNbtService());
    }

    void register(Location location, BlockDefinition definition, ItemStack placedFrom) {
        Location blockLocation = location.getBlock().getLocation();
        unregister(blockLocation);

        double radius = resolveCollectRadius(definition);
        double depth = resolveCollectDepth(definition);
        boolean showIndicator = resolveShowIndicator(definition);
        Component title = definition.getTitle() == null ? Component.text("Quarry Cache") : definition.getTitle();

        QuarryCacheInventory inventory = new QuarryCacheInventory(blockLocation, title);
        inventory.setOnChanged(value -> persist(blockLocation, value));

        if (placedFrom != null && storage.hasStoredContents(placedFrom)) {
            storage.restoreFromItem(placedFrom, inventory);
        } else {
            storage.applyContents(inventory, storage.load(blockLocation));
        }

        extensionSupport.registerCustomInventory(inventory.getInventory(), inventory);
        QuarryCacheData cache = new QuarryCacheData(blockLocation, radius, depth, showIndicator, inventory);
        caches.put(blockLocation, cache);
        extensionSupport.registerDropCollector(blockLocation, (breakLocation, drops) -> tryCollect(cache, breakLocation, drops));
        persist(blockLocation, inventory);

        if (showIndicator) {
            indicatorTasks.put(blockLocation, Bukkit.getScheduler().runTaskTimer(
                    plugin, () -> QuarryCacheZoneIndicator.spawn(cache), 20L, 40L));
        }
    }

    void handleBreak(Location location, ItemStack droppedItem) {
        Location blockLocation = location.getBlock().getLocation();
        QuarryCacheData cache = caches.get(blockLocation);

        if (cache != null) {
            persist(blockLocation, cache.inventory);
        }

        boolean attachedToItem = false;
        if (droppedItem != null) {
            if (cache != null) {
                storage.attachContentsToItem(droppedItem, cache.inventory);
                attachedToItem = storage.hasStoredContents(droppedItem);
            } else {
                QuarryCacheContents contents = storage.load(blockLocation);
                if (!contents.isEmpty()) {
                    storage.attachContentsToItem(droppedItem, contents);
                    attachedToItem = storage.hasStoredContents(droppedItem);
                }
            }
        }

        unregister(blockLocation);

        if (attachedToItem || storage.load(blockLocation).isEmpty()) {
            storage.delete(blockLocation);
        }
    }

    void unregister(Location location) {
        Location blockLocation = location.getBlock().getLocation();
        QuarryCacheData cache = caches.remove(blockLocation);
        extensionSupport.unregisterDropCollector(blockLocation);
        BukkitTask indicatorTask = indicatorTasks.remove(blockLocation);
        if (indicatorTask != null) {
            indicatorTask.cancel();
        }
        if (cache != null) {
            extensionSupport.unregisterCustomInventory(cache.inventory.getInventory());
        }
    }

    void openGui(Player player, Location location) {
        QuarryCacheData cache = caches.get(location.getBlock().getLocation());
        if (cache == null) {
            return;
        }
        cache.inventory.restoreDecorations();
        player.openInventory(cache.inventory.getInventory());
    }

    private boolean tryCollect(QuarryCacheData cache, Location breakLocation, Collection<ItemStack> drops) {
        if (!cache.isWithinRadius(breakLocation)) {
            return false;
        }
        return tryStore(cache, drops);
    }

    private boolean tryStore(QuarryCacheData cache, Collection<ItemStack> drops) {
        boolean storedAny = false;
        Inventory inventory = cache.inventory.getInventory();
        Iterator<ItemStack> iterator = drops.iterator();

        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (drop == null || drop.getType().isAir()) {
                iterator.remove();
                continue;
            }
            if (!cache.inventory.accepts(drop)) {
                continue;
            }

            ItemStack remaining = storeInStorage(inventory, drop.clone());
            if (remaining == null || remaining.getAmount() <= 0) {
                iterator.remove();
                storedAny = true;
            } else if (remaining.getAmount() < drop.getAmount()) {
                drop.setAmount(remaining.getAmount());
                storedAny = true;
            }
        }

        if (storedAny) {
            cache.inventory.notifyChanged();
        }
        return storedAny;
    }

    private ItemStack storeInStorage(Inventory inventory, ItemStack stack) {
        for (int slot = QuarryCacheInventory.STORAGE_START; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            ItemStack existing = inventory.getItem(slot);
            if (existing == null || existing.getType().isAir()) {
                inventory.setItem(slot, stack);
                return null;
            }
            if (existing.isSimilar(stack) && existing.getAmount() < existing.getMaxStackSize()) {
                int transferable = Math.min(stack.getAmount(), existing.getMaxStackSize() - existing.getAmount());
                existing.setAmount(existing.getAmount() + transferable);
                stack.setAmount(stack.getAmount() - transferable);
                if (stack.getAmount() <= 0) {
                    return null;
                }
            }
        }

        for (int slot = QuarryCacheInventory.STORAGE_START; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            ItemStack existing = inventory.getItem(slot);
            if (existing == null || existing.getType().isAir()) {
                inventory.setItem(slot, stack);
                return null;
            }
        }
        return stack;
    }

    private void persist(Location location, QuarryCacheInventory inventory) {
        storage.save(location, inventory);
    }

    private double resolveCollectRadius(BlockDefinition definition) {
        Map<String, Object> customData = definition.getCustomData();
        if (customData.containsKey("collectRadius")) {
            return StrategySupport.customDouble(customData, "collectRadius", 5.0);
        }
        return StrategySupport.customDouble(customData, "collect_radius", 5.0);
    }

    private double resolveCollectDepth(BlockDefinition definition) {
        Map<String, Object> customData = definition.getCustomData();
        if (customData.containsKey("collectDepth")) {
            return StrategySupport.customDouble(customData, "collectDepth", 5.0);
        }
        return StrategySupport.customDouble(customData, "collect_depth", 5.0);
    }

    private boolean resolveShowIndicator(BlockDefinition definition) {
        Map<String, Object> customData = definition.getCustomData();
        if (customData.containsKey("showCollectZone")) {
            return StrategySupport.customBoolean(customData, "showCollectZone", true);
        }
        return StrategySupport.customBoolean(customData, "show_collect_zone", true);
    }
}
