package dev.rono.igniscore.paper.adapter;

import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import dev.rono.igniscore.paper.command.PaperBasicCommandAdapter;
import dev.rono.igniscore.platform.paper.PaperPlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlatformAdapter extends BukkitPlatformAdapter {
    private final PaperBasicCommandAdapter ignisCommandBridge;

    public PaperPlatformAdapter(JavaPlugin plugin) {
        super(plugin, new PaperPlatformHooks());
        this.ignisCommandBridge = new PaperBasicCommandAdapter(IgnisCommands.IGNIS, IgnisCommands.PERMISSION);
        plugin.registerCommand(
                IgnisCommands.IGNIS,
                IgnisCommands.DESCRIPTION,
                IgnisCommands.ALIASES,
                ignisCommandBridge);
    }

    @Override
    public void registerCommand(String name, Object commandExecutor) {
        if (!IgnisCommands.IGNIS.equals(name) || !(commandExecutor instanceof PluginCommandHandler handler)) {
            return;
        }
        ignisCommandBridge.bind(handler);
    }
}
