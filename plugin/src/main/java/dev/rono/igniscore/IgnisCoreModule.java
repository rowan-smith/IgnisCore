package dev.rono.igniscore;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.core.IgnisStrategyContextProvider;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ItemListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.BundledExtensionExtractor;
import dev.rono.igniscore.loader.ExtensionResourceProvider;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.resourcepack.ResourcePackBuilder;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.BlockInteractionResolver;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.BlockItemIdentifier;
import dev.rono.igniscore.service.ConfiguredEffectService;
import dev.rono.igniscore.service.CustomBlockBreakService;
import dev.rono.igniscore.service.CustomBlockIgnitionService;
import dev.rono.igniscore.service.CustomBlockPlacementService;
import dev.rono.igniscore.service.ItemFactory;
import dev.rono.igniscore.service.ItemIdentifier;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.StrategyProfileResolver;
import dev.rono.igniscore.service.VisualEffectService;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class IgnisCoreModule extends AbstractModule {
    private final Main plugin;

    public IgnisCoreModule(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Main.class).toInstance(plugin);
        bind(JavaPlugin.class).toInstance(plugin);
        bind(Plugin.class).toInstance(plugin);

        bind(CommandRegistrar.class).in(Scopes.SINGLETON);
        bind(IgnisCommand.class).in(Scopes.SINGLETON);
        bind(BlockListener.class).in(Scopes.SINGLETON);
        bind(ItemListener.class).in(Scopes.SINGLETON);
        bind(ResourcePackStatusListener.class).in(Scopes.SINGLETON);

        bind(IgnisStrategyRegistry.class).to(IgnisStrategyRegistryImpl.class).in(Scopes.SINGLETON);
        bind(IgnisStrategyContext.class).toProvider(IgnisStrategyContextProvider.class).in(Scopes.SINGLETON);

        bind(NBTService.class).in(Scopes.SINGLETON);
        bind(ProtocolService.class).in(Scopes.SINGLETON);
        bind(RuntimeBlockService.class).in(Scopes.SINGLETON);
        bind(VisualEffectService.class).in(Scopes.SINGLETON);
        bind(BlockManager.class).in(Scopes.SINGLETON);
        bind(ItemManager.class).in(Scopes.SINGLETON);
        bind(ResourcePackBuilder.class).in(Scopes.SINGLETON);
        bind(ResourcePackService.class).in(Scopes.SINGLETON);
        bind(BlockItemFactory.class).in(Scopes.SINGLETON);
        bind(ItemFactory.class).in(Scopes.SINGLETON);
        bind(BlockItemIdentifier.class).in(Scopes.SINGLETON);
        bind(ItemIdentifier.class).in(Scopes.SINGLETON);
        bind(BlockInteractionResolver.class).in(Scopes.SINGLETON);
        bind(ConfiguredEffectService.class).in(Scopes.SINGLETON);
        bind(StrategyProfileResolver.class).in(Scopes.SINGLETON);
        bind(CustomBlockPlacementService.class).in(Scopes.SINGLETON);
        bind(CustomBlockBreakService.class).in(Scopes.SINGLETON);
        bind(CustomBlockIgnitionService.class).in(Scopes.SINGLETON);
        bind(ExtensionResourceProvider.class).in(Scopes.SINGLETON);
        bind(BundledExtensionExtractor.class).in(Scopes.SINGLETON);
        bind(BlockExtensionLoader.class).in(Scopes.SINGLETON);
        bind(ItemExtensionLoader.class).in(Scopes.SINGLETON);
        bind(ExtensionBootstrap.class).in(Scopes.SINGLETON);
    }
}
