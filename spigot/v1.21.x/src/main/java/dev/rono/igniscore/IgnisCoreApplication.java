package dev.rono.igniscore;

import com.google.inject.Inject;
import dev.rono.igniscore.api.IgnisCoreFacade;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ExtensionSupportListener;
import dev.rono.igniscore.listener.ItemListener;
import dev.rono.igniscore.listener.PlacedBlockRestoreListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.ItemFactory;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.ExtensionSupportService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class IgnisCoreApplication implements IgnisCoreFacade {
    private final Main plugin;
    private final CommandRegistrar commandRegistrar;
    private final IgnisCommand ignisCommand;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final ResourcePackService resourcePackService;
    private final BlockItemFactory blockItemFactory;
    private final ItemFactory itemFactory;
    private final NBTService nbtService;
    private final ProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final RuntimeBlockService runtimeBlockService;
    private final VisualEffectService visualEffectService;
    private final ExtensionBootstrap extensionBootstrap;
    private final IgnisStrategyRegistry strategyRegistry;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final ExtensionSupportService extensionSupportService;
    private final PlacedBlockRestoreListener placedBlockRestoreListener;
    private final List<Listener> listeners;

    @Inject
    public IgnisCoreApplication(Main plugin,
                                CommandRegistrar commandRegistrar,
                                IgnisCommand ignisCommand,
                                BlockListener blockListener,
                                ItemListener itemListener,
                                ExtensionSupportListener extensionSupportListener,
                                ResourcePackStatusListener resourcePackStatusListener,
                                BlockManager blockManager,
                                ItemManager itemManager,
                                ResourcePackService resourcePackService,
                                BlockItemFactory blockItemFactory,
                                ItemFactory itemFactory,
                                NBTService nbtService,
                                ProtocolService protocolService,
                                IgnisEffectService effectService,
                                RuntimeBlockService runtimeBlockService,
                                VisualEffectService visualEffectService,
                                ExtensionBootstrap extensionBootstrap,
                                IgnisStrategyRegistry strategyRegistry,
                                BlockExtensionLoader blockExtensionLoader,
                                ItemExtensionLoader itemExtensionLoader,
                                ExtensionSupportService extensionSupportService,
                                PlacedBlockRestoreListener placedBlockRestoreListener) {
        this.plugin = plugin;
        this.commandRegistrar = commandRegistrar;
        this.ignisCommand = ignisCommand;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.resourcePackService = resourcePackService;
        this.blockItemFactory = blockItemFactory;
        this.itemFactory = itemFactory;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.runtimeBlockService = runtimeBlockService;
        this.visualEffectService = visualEffectService;
        this.extensionBootstrap = extensionBootstrap;
        this.strategyRegistry = strategyRegistry;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.extensionSupportService = extensionSupportService;
        this.placedBlockRestoreListener = placedBlockRestoreListener;
        this.listeners = List.of(itemListener, blockListener, extensionSupportListener,
                resourcePackStatusListener, placedBlockRestoreListener);
    }

    public void enable() {
        extensionBootstrap.loadAll();
        registerListeners();
        placedBlockRestoreListener.restoreLoadedChunks();
        commandRegistrar.register("ignis", ignisCommand);
        initializeResourcePack();
    }

    public void disable() {
        blockExtensionLoader.unloadAll();
        itemExtensionLoader.unloadAll();
        extensionSupportService.clear();
        blockManager.cleanup();
        resourcePackService.stopServer();
    }

    @Override
    public Map<String, BlockDefinition> getBlockTypes() {
        return blockManager.getBlockTypes();
    }

    @Override
    public Map<String, ItemDefinition> getItemTypes() {
        return itemManager.getItemTypes();
    }

    @Override
    public RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
        return blockManager.triggerBlock(BukkitBridge.toBukkit(location), typeId, context);
    }

    @Override
    public RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context) {
        org.bukkit.Location bukkitLocation = BukkitBridge.toBukkit(location);
        String typeId = blockManager.getPlacedBlockType(bukkitLocation);
        if (typeId == null) {
            return null;
        }

        blockManager.unregisterPlacedBlock(bukkitLocation);
        Block block = bukkitLocation.getBlock();
        if (block.getType() != Material.AIR) {
            block.setType(Material.AIR);
        }
        return blockManager.triggerBlock(bukkitLocation, typeId, context);
    }

    @Override
    public String getPlacedBlockType(IgnisLocation location) {
        return blockManager.getPlacedBlockType(BukkitBridge.toBukkit(location));
    }

    @Override
    public Collection<RuntimeBlockInstance> getActiveBlocks() {
        return blockManager.getActiveBlocks();
    }

    @Override
    public IgnisItem createBlockItem(String typeId) {
        return BukkitBridge.wrap(blockItemFactory.createBlockItem(typeId));
    }

    @Override
    public IgnisItem createItem(String typeId) {
        return BukkitBridge.wrap(itemFactory.createItem(typeId));
    }

    @Override
    public IgnisStrategyRegistry getStrategyRegistry() {
        return strategyRegistry;
    }

    @Override
    public IgnisNbtService getNbtService() {
        return nbtService;
    }

    @Override
    public IgnisProtocolService getProtocolService() {
        return protocolService;
    }

    @Override
    public IgnisEffectService getEffectService() {
        return effectService;
    }

    @Override
    public void reloadExtensions() {
        extensionBootstrap.reloadAll();
        try {
            resourcePackService.buildAndRegister();
            resourcePackService.reloadConfiguration();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to rebuild resource pack after reload: " + e.getMessage());
        }
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

    public NBTService getNbtServiceImpl() {
        return nbtService;
    }

    public ProtocolService getProtocolServiceImpl() {
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

    public BlockExtensionLoader getBlockExtensionLoader() {
        return blockExtensionLoader;
    }

    public ItemExtensionLoader getItemExtensionLoader() {
        return itemExtensionLoader;
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
