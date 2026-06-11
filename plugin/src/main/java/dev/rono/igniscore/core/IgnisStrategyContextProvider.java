package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.service.NBTService;

public class IgnisStrategyContextProvider implements Provider<IgnisStrategyContext> {
    private final Main plugin;
    private final NBTService nbtService;

    @Inject
    public IgnisStrategyContextProvider(Main plugin, NBTService nbtService) {
        this.plugin = plugin;
        this.nbtService = nbtService;
    }

    @Override
    public IgnisStrategyContext get() {
        return new IgnisStrategyContext(plugin, nbtService);
    }
}
