package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class PaperCommandSupport {
    private static final Map<String, PaperBasicCommandAdapter> BRIDGES = new ConcurrentHashMap<>();

    private PaperCommandSupport() {
    }

    public static void installBootstrap(PluginMeta meta, LifecycleEventManager<BootstrapContext> lifecycleManager) {
        PaperBasicCommandAdapter bridge = createBridge();
        BRIDGES.put(meta.getName(), bridge);
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Set<String> labels = event.registrar().register(
                    IgnisCommands.IGNIS,
                    IgnisCommands.DESCRIPTION,
                    IgnisCommands.ALIASES,
                    bridge);
            logger(meta).info("Registered IgnisCore Paper commands: " + String.join(", ", labels));
        });
    }

    public static void installOnEnable(JavaPlugin plugin) {
        if (BRIDGES.containsKey(plugin.getName())) {
            return;
        }

        PaperBasicCommandAdapter bridge = createBridge();
        BRIDGES.put(plugin.getName(), bridge);
        plugin.registerCommand(
                IgnisCommands.IGNIS,
                IgnisCommands.DESCRIPTION,
                IgnisCommands.ALIASES,
                bridge);
        plugin.getLogger().info("Registered IgnisCore Paper command bridge during onEnable");
    }

    public static void bind(JavaPlugin plugin, PluginCommandHandler handler) {
        PaperBasicCommandAdapter bridge = BRIDGES.get(plugin.getName());
        if (bridge == null) {
            plugin.getLogger().warning("IgnisCore Paper command bridge was not installed");
            return;
        }
        bridge.bind(handler);
    }

    private static PaperBasicCommandAdapter createBridge() {
        return new PaperBasicCommandAdapter(IgnisCommands.IGNIS, IgnisCommands.PERMISSION);
    }

    private static Logger logger(PluginMeta meta) {
        return Logger.getLogger(meta.getName());
    }
}
