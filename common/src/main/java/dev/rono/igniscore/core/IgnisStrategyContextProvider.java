package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class IgnisStrategyContextProvider implements Provider<IgnisStrategyContext> {
    private final PlatformAdapter platformAdapter;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final IgnisRegionService regionService;
    private final IgnisHologramService hologramService;
    private final IgnisNpcService npcService;
    private final IgnisIntegrationRegistry integrationRegistry;
    private final ExtensionSupport extensionSupport;
    private final IgnisEventBus eventBus;

    @Inject
    public IgnisStrategyContextProvider(PlatformAdapter platformAdapter,
                                         IgnisNbtService nbtService,
                                         IgnisProtocolService protocolService,
                                         IgnisEffectService effectService,
                                         IgnisRegionService regionService,
                                         IgnisHologramService hologramService,
                                         IgnisNpcService npcService,
                                         IgnisIntegrationRegistry integrationRegistry,
                                         ExtensionSupport extensionSupport,
                                         IgnisEventBus eventBus) {
        this.platformAdapter = platformAdapter;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.regionService = regionService;
        this.hologramService = hologramService;
        this.npcService = npcService;
        this.integrationRegistry = integrationRegistry;
        this.extensionSupport = extensionSupport;
        this.eventBus = eventBus;
    }

    @Override
    public IgnisStrategyContext get() {
        return new IgnisStrategyContext(
                platformAdapter.scheduler(),
                nbtService,
                protocolService,
                effectService,
                regionService,
                hologramService,
                npcService,
                integrationRegistry,
                extensionSupport,
                eventBus);
    }
}
