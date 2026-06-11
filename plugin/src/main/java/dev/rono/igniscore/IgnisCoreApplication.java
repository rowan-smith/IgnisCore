package dev.rono.igniscore;

import com.google.inject.Inject;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
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
    private final ItemManager itemManager;
    private final ResourcePackService resourcePackService;
    private final BlockItemFactory blockItemFactory;
    private final NBTService nbtService;
    private final ProtocolService protocolService;
    private final RuntimeBlockService runtimeBlockService;
    private final VisualEffectService visualEffectService;
    private final ExtensionBootstrap extensionBootstrap;
    private final IgnisStrategyRegistry strategyRegistry;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final List<Listener> listeners;

    @Inject
    public IgnisCoreApplication(Main plugin,
                                CommandRegistrar commandRegistrar,
                                IgnisCommand ignisCommand,
                                BlockListener blockListener,
                                ResourcePackStatusListener resourcePackStatusListener,
                                BlockManager blockManager,
                                ItemManager itemManager,
                                ResourcePackService resourcePackService,
                                BlockItemFactory blockItemFactory,
                                NBTService nbtService,
                                ProtocolService protocolService,
                                RuntimeBlockService runtimeBlockService,
                                VisualEffectService visualEffectService,
                                ExtensionBootstrap extensionBootstrap,
                                IgnisStrategyRegistry strategyRegistry,
                                BlockExtensionLoader blockExtensionLoader,
                                ItemExtensionLoader itemExtensionLoader) {
        this.plugin = plugin;
        this.commandRegistrar = commandRegistrar;
        this.ignisCommand = ignisCommand;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.resourcePackService = resourcePackService;
        this.blockItemFactory = blockItemFactory;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.runtimeBlockService = runtimeBlockService;
        this.visualEffectService = visualEffectService;
        this.extensionBootstrap = extensionBootstrap;
        this.strategyRegistry = strategyRegistry;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.listeners = List.of(blockListener, resourcePackStatusListener);
    }

    public void enable() {
        extensionBootstrap.loadAll();
        registerListeners();
        commandRegistrar.register("ignis", ignisCommand);
        initializeResourcePack();
    }

    public void disable() {
        blockExtensionLoader.unloadAll();
        itemExtensionLoader.unloadAll();
        blockManager.cleanup();
        resourcePackService.stopServer();
    }

    public BlockItemFactory getBlockItemFactory() {
        return blockItemFactory;
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
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

    public BlockExtensionLoader getBlockExtensionLoader() {
        return blockExtensionLoader;
    }

    public ItemExtensionLoader getItemExtensionLoader() {
        return itemExtensionLoader;
    }

    public void reloadExtensions() {
        extensionBootstrap.reloadAll();
        try {
            resourcePackService.buildAndRegister();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to rebuild resource pack after reload: " + e.getMessage());
        }
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
