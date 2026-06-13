package dev.rono.igniscore.spigot.platform;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisPlatformIntegration;
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ExtensionSupportListener;
import dev.rono.igniscore.listener.ItemListener;
import dev.rono.igniscore.listener.PlacedBlockRestoreListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SpigotPlatformIntegration implements IgnisPlatformIntegration {
    private final JavaPlugin plugin;
    private final CommandRegistrar commandRegistrar;
    private final IgnisCommand ignisCommand;
    private final PlacedBlockRestoreListener placedBlockRestoreListener;
    private final List<Listener> listeners;

    @Inject
    public SpigotPlatformIntegration(JavaPlugin plugin,
                                     CommandRegistrar commandRegistrar,
                                     IgnisCommand ignisCommand,
                                     BlockListener blockListener,
                                     ItemListener itemListener,
                                     ExtensionSupportListener extensionSupportListener,
                                     ResourcePackStatusListener resourcePackStatusListener,
                                     PlacedBlockRestoreListener placedBlockRestoreListener) {
        this.plugin = plugin;
        this.commandRegistrar = commandRegistrar;
        this.ignisCommand = ignisCommand;
        this.placedBlockRestoreListener = placedBlockRestoreListener;
        this.listeners = List.of(itemListener, blockListener, extensionSupportListener,
                resourcePackStatusListener, placedBlockRestoreListener);
    }

    @Override
    public void onRuntimeEnable() {
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        placedBlockRestoreListener.restoreLoadedChunks();
        commandRegistrar.register("ignis", ignisCommand);
    }

    @Override
    public void onRuntimeDisable() {
    }
}
