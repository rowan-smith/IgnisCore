package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;

/**
 * Single entry point for services injected into extension strategies.
 */
public final class IgnisStrategyContext {
    private final IgnisScheduler scheduler;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionSupport extensionSupport;
    private final IgnisEventBus eventBus;

    public IgnisStrategyContext(IgnisScheduler scheduler,
                                IgnisNbtService nbtService,
                                IgnisProtocolService protocolService,
                                IgnisEffectService effectService,
                                ExtensionSupport extensionSupport,
                                IgnisEventBus eventBus) {
        this.scheduler = scheduler;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionSupport = extensionSupport;
        this.eventBus = eventBus;
    }

    public IgnisScheduler scheduler() {
        return scheduler;
    }

    public IgnisNbtService nbt() {
        return nbtService;
    }

    public IgnisEffectService effects() {
        return effectService;
    }

    public IgnisProtocolService protocol() {
        return protocolService;
    }

    public ExtensionSupport extensions() {
        return extensionSupport;
    }

    public IgnisEventBus eventBus() {
        return eventBus;
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

    public IgnisEventBus getEventBus() {
        return eventBus;
    }
}
