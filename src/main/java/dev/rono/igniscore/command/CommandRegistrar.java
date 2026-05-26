package dev.rono.igniscore.command;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class CommandRegistrar {
    private final JavaPlugin plugin;

    public CommandRegistrar(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(String name, PluginCommandHandler handler) {
        PluginCommand command = Objects.requireNonNull(plugin.getCommand(name),
                "Command missing from plugin.yml: " + name);
        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }
}
