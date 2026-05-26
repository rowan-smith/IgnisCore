package dev.rono.igniscore;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class Main extends JavaPlugin {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private BlockManager blockManager;
    private ResourcePackService resourcePackService;
    private BlockItemFactory blockItemFactory;
    private boolean debugEnabled = false;

    private NBTService nbtService;
    private ProtocolService protocolService;
    private RuntimeBlockService runtimeBlockService;
    private VisualEffectService visualEffectService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        initializeServices();
        IgnisCoreAPI.init(this);

        registerListeners();
        registerCommands();
        initializeResourcePack();
    }

    @Override
    public void onDisable() {
        if (blockManager != null) {
            blockManager.cleanup();
        }
        if (resourcePackService != null) {
            resourcePackService.stopServer();
        }
    }

    public ItemStack createBlockItem(String typeId) {
        return blockItemFactory.createBlockItem(typeId);
    }

    public Component message(String message) {
        return miniMessage.deserialize(message);
    }

    public void debug(String message) {
        if (debugEnabled) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
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

    private void initializeServices() {
        nbtService = new NBTService();
        protocolService = new ProtocolService(this);
        runtimeBlockService = new RuntimeBlockService();
        visualEffectService = new VisualEffectService(protocolService);

        blockManager = new BlockManager(this);
        resourcePackService = new ResourcePackService(this, blockManager);
        blockItemFactory = new BlockItemFactory(blockManager, nbtService);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this, blockManager), this);
        getServer().getPluginManager().registerEvents(new ResourcePackStatusListener(this), this);
    }

    private void registerCommands() {
        new CommandRegistrar(this).register("ignis",
                new IgnisCommand(this, blockManager, resourcePackService));
    }

    private void initializeResourcePack() {
        try {
            resourcePackService.buildAndRegister();
            resourcePackService.startServer();
        } catch (IOException e) {
            getLogger().severe("Failed to generate resource pack: " + e.getMessage());
        }
    }
}
