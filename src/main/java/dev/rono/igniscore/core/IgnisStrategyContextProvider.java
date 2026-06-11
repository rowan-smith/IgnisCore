package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.service.ConfiguredEffectService;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;

public class IgnisStrategyContextProvider implements Provider<IgnisStrategyContext> {
    private final Main plugin;
    private final ConfiguredEffectService effectService;
    private final NBTService nbtService;
    private final RuntimeBlockService runtimeBlockService;
    private final VisualEffectService visualEffectService;

    @Inject
    public IgnisStrategyContextProvider(Main plugin,
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

    @Override
    public IgnisStrategyContext get() {
        return new IgnisStrategyContext(plugin, effectService, nbtService, runtimeBlockService, visualEffectService);
    }
}
