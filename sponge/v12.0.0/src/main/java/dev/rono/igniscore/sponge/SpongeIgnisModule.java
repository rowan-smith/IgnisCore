package dev.rono.igniscore.sponge;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.command.SpongeIgnisCommand;
import dev.rono.igniscore.sponge.core.SpongeExtensionBootstrap;
import dev.rono.igniscore.sponge.core.SpongeStrategyContextProvider;
import dev.rono.igniscore.sponge.core.SpongeStrategyRegistryImpl;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.loader.SpongeBlockExtensionLoader;
import dev.rono.igniscore.sponge.loader.SpongeBundledExtensionExtractor;
import dev.rono.igniscore.sponge.loader.SpongeExtensionLoadEngine;
import dev.rono.igniscore.sponge.loader.SpongeItemExtensionLoader;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;
import dev.rono.igniscore.sponge.service.SpongeExtensionSupportService;
import dev.rono.igniscore.sponge.service.SpongeItemFactory;
import dev.rono.igniscore.sponge.service.SpongeItemIdentifier;
import dev.rono.igniscore.sponge.service.SpongeItemManager;
import dev.rono.igniscore.sponge.service.SpongeNbtService;
import dev.rono.igniscore.sponge.service.SpongeNoopEffectService;
import dev.rono.igniscore.sponge.service.SpongeNoopProtocolService;

public class SpongeIgnisModule extends AbstractModule {
    private final IgnisSpongePlugin plugin;
    private final SpongePlatformAdapter platformAdapter;

    public SpongeIgnisModule(IgnisSpongePlugin plugin, SpongePlatformAdapter platformAdapter) {
        this.plugin = plugin;
        this.platformAdapter = platformAdapter;
    }

    @Override
    protected void configure() {
        bind(IgnisSpongePlugin.class).toInstance(plugin);
        bind(SpongePlatformAdapter.class).toInstance(platformAdapter);
        bind(PlatformAdapter.class).toInstance(platformAdapter);

        bind(SpongeIgnisCommand.class).in(Scopes.SINGLETON);
        bind(SpongeItemListener.class).in(Scopes.SINGLETON);
        bind(SpongeBlockListener.class).in(Scopes.SINGLETON);

        bind(IgnisStrategyRegistry.class).to(SpongeStrategyRegistryImpl.class).in(Scopes.SINGLETON);
        bind(IgnisStrategyContext.class).toProvider(SpongeStrategyContextProvider.class).in(Scopes.SINGLETON);

        bind(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(IgnisNbtService.class).to(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopProtocolService.class).in(Scopes.SINGLETON);
        bind(IgnisProtocolService.class).to(SpongeNoopProtocolService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopEffectService.class).in(Scopes.SINGLETON);
        bind(IgnisEffectService.class).to(SpongeNoopEffectService.class).in(Scopes.SINGLETON);

        bind(SpongeBlockManager.class).in(Scopes.SINGLETON);
        bind(SpongeItemManager.class).in(Scopes.SINGLETON);
        bind(SpongeItemFactory.class).in(Scopes.SINGLETON);
        bind(SpongeItemIdentifier.class).in(Scopes.SINGLETON);
        bind(SpongeExtensionSupportService.class).in(Scopes.SINGLETON);
        bind(ExtensionSupport.class).to(SpongeExtensionSupportService.class).in(Scopes.SINGLETON);

        bind(SpongeBundledExtensionExtractor.class).in(Scopes.SINGLETON);
        bind(SpongeExtensionLoadEngine.class).in(Scopes.SINGLETON);
        bind(SpongeBlockExtensionLoader.class).in(Scopes.SINGLETON);
        bind(SpongeItemExtensionLoader.class).in(Scopes.SINGLETON);
        bind(SpongeExtensionBootstrap.class).in(Scopes.SINGLETON);

        bind(SpongeIgnisFacade.class).in(Scopes.SINGLETON);
        bind(SpongeIgnisApplication.class).in(Scopes.SINGLETON);
    }
}
