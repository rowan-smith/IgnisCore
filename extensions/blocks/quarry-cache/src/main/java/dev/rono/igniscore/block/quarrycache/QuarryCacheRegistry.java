package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class QuarryCacheRegistry {
    private final IgnisStrategyContext context;
    private final ExtensionSupport extensionSupport;
    private final QuarryCacheStorage storage;
    private final Map<IgnisLocation, QuarryCacheData> caches = new ConcurrentHashMap<>();
    private final Map<IgnisLocation, IgnisTask> indicatorTasks = new ConcurrentHashMap<>();

    QuarryCacheRegistry(IgnisStrategyContext context) {
        this.context = context;
        this.extensionSupport = context.extensions();
        this.storage = new QuarryCacheStorage(extensionSupport, context.nbt());
    }

    void register(IgnisLocation location, BlockDefinition definition, IgnisItem placedFrom) {
        var blockLocation = Locations.toBlock(location);
        unregister(blockLocation);

        var radius = resolveCollectRadius(definition);
        var depth = resolveCollectDepth(definition);
        var showIndicator = resolveShowIndicator(definition);
        var title = definition.getTitle() == null ? Component.text("Quarry Cache") : definition.getTitle();

        var inventory = new QuarryCacheInventory(blockLocation, title, extensionSupport);
        inventory.setOnChanged(value -> persist(blockLocation, value));

        if (storage.hasStoredContents(placedFrom)) {
            storage.restoreFromItem(placedFrom, inventory);
        } else {
            storage.applyContents(inventory, storage.load(blockLocation));
        }

        extensionSupport.registerCustomInventory(inventory.getInventory().nativeInventory(), inventory);

        var cache = new QuarryCacheData(blockLocation, radius, depth, showIndicator, inventory);

        caches.put(blockLocation, cache);
        extensionSupport.registerDropCollector(blockLocation, (breakLocation, drops) -> tryCollect(cache, breakLocation, drops));
        persist(blockLocation, inventory);

        if (showIndicator) {
            indicatorTasks.put(blockLocation, context.scheduler().runRepeating(
                    cache.center(),
                    () -> QuarryCacheZoneIndicator.spawn(cache, extensionSupport),
                    20L,
                    40L));
        }
    }

    void handleBreak(IgnisLocation location, IgnisItem droppedItem) {
        var blockLocation = Locations.toBlock(location);
        var cache = caches.get(blockLocation);

        if (cache != null) {
            persist(blockLocation, cache.inventory());
        }

        var attachedToItem = false;
        if (droppedItem != null && !droppedItem.isAir()) {
            if (cache != null) {
                storage.attachContentsToItem(droppedItem, cache.inventory());
                attachedToItem = storage.hasStoredContents(droppedItem);
            } else {
                var contents = storage.load(blockLocation);
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

    void unregister(IgnisLocation location) {
        var blockLocation = Locations.toBlock(location);
        var cache = caches.remove(blockLocation);

        extensionSupport.unregisterDropCollector(blockLocation);

        var indicatorTask = indicatorTasks.remove(blockLocation);
        if (indicatorTask != null) {
            indicatorTask.cancel();
        }

        if (cache != null) {
            extensionSupport.unregisterCustomInventory(cache.inventory().getInventory().nativeInventory());
        }
    }

    void openGui(IgnisPlayer player, IgnisLocation location) {
        var cache = caches.get(Locations.toBlock(location));
        if (cache == null) {
            return;
        }

        cache.inventory().restoreDecorations();
        extensionSupport.openInventory(player, cache.inventory().getInventory());
    }

    private boolean tryCollect(QuarryCacheData cache, IgnisLocation breakLocation, Collection<IgnisItem> drops) {
        if (!cache.isWithinRadius(breakLocation)) {
            return false;
        }
        return tryStore(cache, drops);
    }

    private boolean tryStore(QuarryCacheData cache, Collection<IgnisItem> drops) {
        var storedAny = false;
        var inventory = cache.inventory().getInventory();
        var iterator = drops.iterator();

        while (iterator.hasNext()) {
            var drop = iterator.next();
            if (drop == null || drop.isAir()) {
                iterator.remove();
                continue;
            }

            if (!cache.inventory().accepts(drop)) {
                continue;
            }

            var remaining = storeInStorage(inventory, drop);
            if (remaining == null || remaining.getAmount() <= 0) {
                iterator.remove();
                storedAny = true;
            } else if (remaining.getAmount() < drop.getAmount()) {
                drop.setAmount(remaining.getAmount());
                storedAny = true;
            }
        }

        if (storedAny) {
            cache.inventory().notifyChanged();
        }

        return storedAny;
    }

    private IgnisItem storeInStorage(dev.rono.igniscore.api.port.IgnisInventory inventory, IgnisItem stack) {
        for (int slot = QuarryCacheInventory.STORAGE_START; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            var existing = inventory.getItem(slot);

            if (existing == null || existing.isAir()) {
                inventory.setItem(slot, stack);
                return null;
            }

            if (existing.getMaterialKey().equals(stack.getMaterialKey())
                    && existing.getAmount() < 64) {
                int transferable = Math.min(stack.getAmount(), 64 - existing.getAmount());
                existing.setAmount(existing.getAmount() + transferable);
                stack.setAmount(stack.getAmount() - transferable);
                inventory.setItem(slot, existing);

                if (stack.getAmount() <= 0) {
                    return null;
                }
            }
        }

        for (int slot = QuarryCacheInventory.STORAGE_START; slot < QuarryCacheInventory.TOTAL_SLOTS; slot++) {
            var existing = inventory.getItem(slot);
            if (existing == null || existing.isAir()) {
                inventory.setItem(slot, stack);
                return null;
            }
        }

        return stack;
    }

    private void persist(IgnisLocation location, QuarryCacheInventory inventory) {
        storage.save(location, inventory);
    }

    private double resolveCollectRadius(BlockDefinition definition) {
        var customData = definition.getCustomData();
        if (customData.containsKey("collectRadius")) {
            return StrategySupport.customDouble(customData, "collectRadius", 5.0);
        }
        return StrategySupport.customDouble(customData, "collect_radius", 5.0);
    }

    private double resolveCollectDepth(BlockDefinition definition) {
        var customData = definition.getCustomData();
        if (customData.containsKey("collectDepth")) {
            return StrategySupport.customDouble(customData, "collectDepth", 5.0);
        }
        return StrategySupport.customDouble(customData, "collect_depth", 5.0);
    }

    private boolean resolveShowIndicator(BlockDefinition definition) {
        var customData = definition.getCustomData();
        if (customData.containsKey("showCollectZone")) {
            return StrategySupport.customBoolean(customData, "showCollectZone", true);
        }
        return StrategySupport.customBoolean(customData, "show_collect_zone", true);
    }
}
