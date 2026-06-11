package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.service.ConfiguredEffectService;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;

/**
 * Services exposed to drop-in strategy plugins.
 */
public final class IgnisStrategyContext {
    private final Main plugin;
    private final ConfiguredEffectService effectService;
    private final NBTService nbtService;
    private final RuntimeBlockService runtimeBlockService;
    private final VisualEffectService visualEffectService;

    public IgnisStrategyContext(Main plugin,
                                ConfiguredEffectService effectService,
                                NBTService nbtService,
                                RuntimeBlockService runtimeBlockService,
                                VisualEffectService visualEffectService) {
        this.plugin = plugin;
        this.effectService = effectService;
        this.nbtService = nbtService;
        this.runtimeBlockService = runtimeBlockService;
        this.visualEffectService = visualEffectService;
    }

    public Main getPlugin() {
        return plugin;
    }

    public ConfiguredEffectService getEffectService() {
        return effectService;
    }

    public NBTService getNbtService() {
        return nbtService;
    }

    public RuntimeBlockService getRuntimeBlockService() {
        return runtimeBlockService;
    }

    public VisualEffectService getVisualEffectService() {
        return visualEffectService;
    }
}
