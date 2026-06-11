package dev.rono.igniscore;

import com.google.inject.Inject;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.ContentPackLoader;
import dev.rono.igniscore.loader.StrategyPluginLoader;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.List;

public class IgnisCoreApplication {
    private final Main plugin;
    private final CommandRegistrar commandRegistrar;
    private final IgnisCommand ignisCommand;
    private final BlockManager blockManager;
    private final ResourcePackService resourcePackService;
    private final BlockItemFactory blockItemFactory;
    private final NBTService nbtService;
    private final ProtocolService protocolService;
    private final RuntimeBlockService runtimeBlockService;
    private final VisualEffectService visualEffectService;
    private final ExtensionBootstrap extensionBootstrap;
    private final IgnisStrategyRegistry strategyRegistry;
    private final StrategyPluginLoader strategyPluginLoader;
    private final ContentPackLoader contentPackLoader;
    private final List<Listener> listeners;

    @Inject
    public IgnisCoreApplication(Main plugin,
                                CommandRegistrar commandRegistrar,
                                IgnisCommand ignisCommand,
                                BlockListener blockListener,
                                ResourcePackStatusListener resourcePackStatusListener,
                                BlockManager blockManager,
                                ResourcePackService resourcePackService,
                                BlockItemFactory blockItemFactory,
                                NBTService nbtService,
                                ProtocolService protocolService,
                                RuntimeBlockService runtimeBlockService,
                                VisualEffectService visualEffectService,
                                ExtensionBootstrap extensionBootstrap,
                                IgnisStrategyRegistry strategyRegistry,
                                StrategyPluginLoader strategyPluginLoader,
                                ContentPackLoader contentPackLoader) {
        this.plugin = plugin;
        this.commandRegistrar = commandRegistrar;
        this.ignisCommand = ignisCommand;
        this.blockManager = blockManager;
        this.resourcePackService = resourcePackService;
        this.blockItemFactory = blockItemFactory;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.runtimeBlockService = runtimeBlockService;
        this.visualEffectService = visualEffectService;
        this.extensionBootstrap = extensionBootstrap;
        this.strategyRegistry = strategyRegistry;
        this.strategyPluginLoader = strategyPluginLoader;
        this.contentPackLoader = contentPackLoader;
        this.listeners = List.of(blockListener, resourcePackStatusListener);
    }

    public void enable() {
        extensionBootstrap.loadAll();
        registerListeners();
        commandRegistrar.register("ignis", ignisCommand);
        initializeResourcePack();
    }

    public void disable() {
        blockManager.cleanup();
        resourcePackService.stopServer();
    }

    public BlockItemFactory getBlockItemFactory() {
        return blockItemFactory;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    public NBTService getNbtService() {
        return nbtService;
    }

    public ProtocolService getProtocolService() {
        return protocolService;
    }

    public RuntimeBlockService getRuntimeBlockService() {
        return runtimeBlockService;
    }

    public VisualEffectService getVisualEffectService() {
        return visualEffectService;
    }

    public ResourcePackService getResourcePackService() {
        return resourcePackService;
    }

    public IgnisStrategyRegistry getStrategyRegistry() {
        return strategyRegistry;
    }

    public StrategyPluginLoader getStrategyPluginLoader() {
        return strategyPluginLoader;
    }

    public ContentPackLoader getContentPackLoader() {
        return contentPackLoader;
    }

    public void reloadExtensions() {
        extensionBootstrap.reloadAll();
    }

    private void registerListeners() {
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void initializeResourcePack() {
        try {
            resourcePackService.buildAndRegister();
            resourcePackService.startServer();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to generate resource pack: " + e.getMessage());
        }
    }
}
