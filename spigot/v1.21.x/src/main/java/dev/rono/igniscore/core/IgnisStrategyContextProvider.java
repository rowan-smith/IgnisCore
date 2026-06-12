package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.spigot.adapter.BukkitIgnisScheduler;

public class IgnisStrategyContextProvider implements Provider<IgnisStrategyContext> {
    private final Main plugin;
    private final NBTService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionSupport extensionSupport;

    @Inject
    public IgnisStrategyContextProvider(Main plugin,
                                         NBTService nbtService,
                                         IgnisProtocolService protocolService,
                                         IgnisEffectService effectService,
                                         ExtensionSupport extensionSupport) {
        this.plugin = plugin;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionSupport = extensionSupport;
    }

    @Override
    public IgnisStrategyContext get() {
        return new IgnisStrategyContext(
                new BukkitIgnisScheduler(plugin),
                nbtService,
                protocolService,
                effectService,
                extensionSupport);
    }
}
