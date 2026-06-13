package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;

import java.util.Map;

/**
 * Services exposed to drop-in strategy plugins.
 */
public final class IgnisStrategyContext {
    private final IgnisScheduler scheduler;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionSupport extensionSupport;

    public IgnisStrategyContext(IgnisScheduler scheduler,
                                IgnisNbtService nbtService,
                                IgnisProtocolService protocolService,
                                IgnisEffectService effectService,
                                ExtensionSupport extensionSupport) {
        this.scheduler = scheduler;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionSupport = extensionSupport;
    }

    public IgnisScheduler getScheduler() {
        return scheduler;
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

    public ExtensionSupport getExtensionSupport() {
        return extensionSupport;
    }
}
