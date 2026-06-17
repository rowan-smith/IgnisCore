package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;

import java.util.Map;

/**
 * Services exposed to drop-in strategy plugins.
 */
public final class IgnisStrategyContext {
    private final IgnisScheduler scheduler;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final IgnisRegionService regionService;
    private final IgnisHologramService hologramService;
    private final IgnisNpcService npcService;
    private final IgnisIntegrationRegistry integrationRegistry;
    private final ExtensionSupport extensionSupport;

    public IgnisStrategyContext(IgnisScheduler scheduler,
                                IgnisNbtService nbtService,
                                IgnisProtocolService protocolService,
                                IgnisEffectService effectService,
                                IgnisRegionService regionService,
                                IgnisHologramService hologramService,
                                IgnisNpcService npcService,
                                IgnisIntegrationRegistry integrationRegistry,
                                ExtensionSupport extensionSupport) {
        this.scheduler = scheduler;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.regionService = regionService;
        this.hologramService = hologramService;
        this.npcService = npcService;
        this.integrationRegistry = integrationRegistry;
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

    public IgnisRegionService getRegionService() {
        return regionService;
    }

    public IgnisHologramService getHologramService() {
        return hologramService;
    }

    public IgnisNpcService getNpcService() {
        return npcService;
    }

    public IgnisIntegrationRegistry getIntegrationRegistry() {
        return integrationRegistry;
    }

    public ExtensionSupport getExtensionSupport() {
        return extensionSupport;
    }
}
