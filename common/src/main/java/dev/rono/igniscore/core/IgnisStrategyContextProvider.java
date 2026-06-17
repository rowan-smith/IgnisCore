package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class IgnisStrategyContextProvider implements Provider<IgnisStrategyContext> {
    private final PlatformAdapter platformAdapter;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionSupport extensionSupport;
    private final IgnisEventBus eventBus;

    @Inject
    public IgnisStrategyContextProvider(PlatformAdapter platformAdapter,
                                         IgnisNbtService nbtService,
                                         IgnisProtocolService protocolService,
                                         IgnisEffectService effectService,
                                         ExtensionSupport extensionSupport,
                                         IgnisEventBus eventBus) {
        this.platformAdapter = platformAdapter;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
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
                extensionSupport,
                eventBus);
    }
}
