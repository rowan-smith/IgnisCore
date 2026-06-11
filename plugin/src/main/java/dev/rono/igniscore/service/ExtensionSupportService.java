package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ExtensionSupportService implements ExtensionSupport {
    private final Map<Location, IgnisDropCollector> collectors = new ConcurrentHashMap<>();

    @Inject
    public ExtensionSupportService() {
    }

    @Override
    public void registerDropCollector(Location location, IgnisDropCollector collector) {
        collectors.put(location.getBlock().getLocation(), collector);
    }

    @Override
    public void unregisterDropCollector(Location location) {
        collectors.remove(location.getBlock().getLocation());
    }

    public boolean tryCollect(Location breakLocation, Collection<ItemStack> drops) {
        for (IgnisDropCollector collector : collectors.values()) {
            if (collector.tryCollect(breakLocation, drops)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        collectors.clear();
    }
}
