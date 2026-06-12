package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class IgnisStrategyContextProvider implements Provider<IgnisStrategyContext> {
    private final PlatformAdapter platformAdapter;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionSupport extensionSupport;

    @Inject
    public IgnisStrategyContextProvider(PlatformAdapter platformAdapter,
                                         IgnisNbtService nbtService,
                                         IgnisProtocolService protocolService,
                                         IgnisEffectService effectService,
                                         ExtensionSupport extensionSupport) {
        this.platformAdapter = platformAdapter;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionSupport = extensionSupport;
    }

    @Override
    public IgnisStrategyContext get() {
        return new IgnisStrategyContext(
                platformAdapter.getScheduler(),
                nbtService,
                protocolService,
                effectService,
                extensionSupport);
    }
}
