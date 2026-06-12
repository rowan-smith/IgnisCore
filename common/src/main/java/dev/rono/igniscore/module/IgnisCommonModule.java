package dev.rono.igniscore.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import dev.rono.igniscore.api.IgnisCoreFacade;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.core.IgnisCoreFacadeImpl;
import dev.rono.igniscore.core.IgnisRuntimeLifecycle;
import dev.rono.igniscore.core.IgnisStrategyContextProvider;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ExtensionLoadEngine;
import dev.rono.igniscore.loader.BundledExtensionExtractor;
import dev.rono.igniscore.loader.ExtensionResourceProvider;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockDefinitionLookup;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.BlockTypeRegistry;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.manager.PlacedBlockRegistry;
import dev.rono.igniscore.resourcepack.ResourcePackBuilder;
import dev.rono.igniscore.service.BlockInteractionResolver;
import dev.rono.igniscore.service.ExtensionSupportService;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.StrategyProfileResolver;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;

public class IgnisCommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IgnisStrategyRegistry.class).to(IgnisStrategyRegistryImpl.class).in(Scopes.SINGLETON);
        bind(IgnisStrategyContext.class).toProvider(IgnisStrategyContextProvider.class).in(Scopes.SINGLETON);

        bind(DefaultExplosionStrategy.class).in(Scopes.SINGLETON);
        bind(RuntimeBlockService.class).in(Scopes.SINGLETON);
        bind(StrategyProfileResolver.class).in(Scopes.SINGLETON);
        bind(BlockInteractionResolver.class).in(Scopes.SINGLETON);
        bind(ItemManager.class).in(Scopes.SINGLETON);
        bind(BlockManager.class).in(Scopes.SINGLETON);
        bind(BlockTypeRegistry.class).to(BlockManager.class).in(Scopes.SINGLETON);
        bind(BlockDefinitionLookup.class).to(BlockManager.class).in(Scopes.SINGLETON);
        bind(PlacedBlockRegistry.class).to(BlockManager.class).in(Scopes.SINGLETON);
        bind(PlacedBlockPersistenceService.class).in(Scopes.SINGLETON);
        bind(ExtensionSupportService.class).in(Scopes.SINGLETON);
        bind(ExtensionSupport.class).to(ExtensionSupportService.class).in(Scopes.SINGLETON);

        bind(IgnisCoreFacade.class).to(IgnisCoreFacadeImpl.class).in(Scopes.SINGLETON);
        bind(IgnisCoreFacadeImpl.class).in(Scopes.SINGLETON);
        bind(IgnisRuntimeLifecycle.class).in(Scopes.SINGLETON);

        bind(ExtensionResourceProvider.class).in(Scopes.SINGLETON);
        bind(BundledExtensionExtractor.class).in(Scopes.SINGLETON);
        bind(ExtensionLoadEngine.class).in(Scopes.SINGLETON);
        bind(BlockExtensionLoader.class).in(Scopes.SINGLETON);
        bind(ItemExtensionLoader.class).in(Scopes.SINGLETON);
        bind(ExtensionBootstrap.class).in(Scopes.SINGLETON);

        bind(ResourcePackBuilder.class).in(Scopes.SINGLETON);
    }
}
