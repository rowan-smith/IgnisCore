package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisQuarryCacheService;
import org.bukkit.plugin.Plugin;

/**
 * Services exposed to drop-in strategy plugins.
 */
public final class IgnisStrategyContext {
    private final Plugin plugin;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final IgnisQuarryCacheService quarryCacheService;

    public IgnisStrategyContext(Plugin plugin,
                                IgnisNbtService nbtService,
                                IgnisProtocolService protocolService,
                                IgnisEffectService effectService,
                                IgnisQuarryCacheService quarryCacheService) {
        this.plugin = plugin;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.quarryCacheService = quarryCacheService;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public IgnisNbtService getNbtService() {
        return nbtService;
    }

    public IgnisProtocolService getProtocolService() {
        return protocolService;
    }

    public IgnisEffectService getEffectService() {
        return effectService;
    }

    public IgnisQuarryCacheService getQuarryCacheService() {
        return quarryCacheService;
    }
}
