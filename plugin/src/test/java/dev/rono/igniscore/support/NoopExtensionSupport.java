package dev.rono.igniscore.support;

import dev.rono.igniscore.api.collection.IgnisDropCollector;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import org.bukkit.Location;

public final class NoopExtensionSupport implements ExtensionSupport {
    public static final ExtensionSupport INSTANCE = new NoopExtensionSupport();

    private NoopExtensionSupport() {
    }

    @Override
    public void registerDropCollector(Location location, IgnisDropCollector collector) {
    }

    @Override
    public void unregisterDropCollector(Location location) {
    }
}
