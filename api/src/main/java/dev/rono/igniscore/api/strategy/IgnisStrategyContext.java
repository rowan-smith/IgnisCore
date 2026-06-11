package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.plugin.Plugin;

/**
 * Services exposed to drop-in strategy plugins.
 */
public final class IgnisStrategyContext {
    private final Plugin plugin;
    private final IgnisNbtService nbtService;

    public IgnisStrategyContext(Plugin plugin, IgnisNbtService nbtService) {
        this.plugin = plugin;
        this.nbtService = nbtService;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public IgnisNbtService getNbtService() {
        return nbtService;
    }
}
