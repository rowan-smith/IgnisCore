package dev.rono.igniscore.spigot.command;

import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public final class BukkitCommandRegistration {
    private BukkitCommandRegistration() {
    }

    public static void register(JavaPlugin plugin, String name, PluginCommandHandler handler) {
        if (!IgnisCommands.IGNIS.equals(name)) {
            plugin.getLogger().warning("Unsupported programmatic command: " + name);
            return;
        }

        PluginCommand existing = plugin.getCommand(name);
        if (existing != null) {
            existing.setExecutor(handler);
            existing.setTabCompleter(handler);
            return;
        }

        PluginCommand command = createPluginCommand(plugin, IgnisCommands.IGNIS);
        command.setDescription(IgnisCommands.DESCRIPTION);
        command.setUsage(IgnisCommands.USAGE);
        command.setAliases(IgnisCommands.ALIASES);
        command.setPermission(IgnisCommands.PERMISSION);
        command.setExecutor(handler);
        command.setTabCompleter(handler);

        CommandMap commandMap = resolveCommandMap(plugin);
        commandMap.register(plugin.getDescription().getName().toLowerCase(), command);
        registerAliasFallbacks(plugin, commandMap, command, IgnisCommands.ALIASES);
        plugin.getLogger().info("Registered IgnisCore Bukkit command /" + IgnisCommands.IGNIS);
    }

    private static PluginCommand createPluginCommand(JavaPlugin plugin, String name) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, org.bukkit.plugin.Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unable to create PluginCommand for " + name, error);
        }
    }

    private static CommandMap resolveCommandMap(JavaPlugin plugin) {
        try {
            Method method = plugin.getServer().getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(plugin.getServer());
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unable to access server command map", error);
        }
    }

    private static void registerAliasFallbacks(JavaPlugin plugin,
                                               CommandMap commandMap,
                                               Command command,
                                               List<String> aliases) {
        for (String alias : aliases) {
            if (commandMap.getCommand(alias) == null) {
                commandMap.register(alias, plugin.getName().toLowerCase(), command);
            }
        }
    }
}
