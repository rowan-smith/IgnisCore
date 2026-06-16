package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.IgnisCommandBridge;
import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class PaperCommandRegistrar {
    private final JavaPlugin plugin;
    private final IgnisCommandBridge bridge = new IgnisCommandBridge();

    public PaperCommandRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
        installLifecycle();
    }

    public void bind(PluginCommandHandler handler) {
        bridge.bind(handler);
    }

    IgnisCommandBridge bridge() {
        return bridge;
    }

    private void installLifecycle() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Set<String> labels = event.registrar().register(
                    IgnisBrigadierTree.build(bridge),
                    IgnisCommands.DESCRIPTION,
                    IgnisCommands.ALIASES);
            plugin.getLogger().info("Registered IgnisCore Paper Brigadier commands: " + String.join(", ", labels));
        });
    }
}
