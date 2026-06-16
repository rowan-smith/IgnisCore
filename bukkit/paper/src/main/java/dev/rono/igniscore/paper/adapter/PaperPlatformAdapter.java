package dev.rono.igniscore.paper.adapter;

import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import dev.rono.igniscore.paper.command.PaperCommandRegistrar;
import dev.rono.igniscore.platform.paper.PaperPlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlatformAdapter extends BukkitPlatformAdapter {
    private final PaperCommandRegistrar commandRegistrar;

    public PaperPlatformAdapter(JavaPlugin plugin) {
        super(plugin, new PaperPlatformHooks());
        this.commandRegistrar = new PaperCommandRegistrar(plugin);
    }

    @Override
    public void registerCommand(String name, Object commandExecutor) {
        if (!IgnisCommands.IGNIS.equals(name) || !(commandExecutor instanceof PluginCommandHandler handler)) {
            return;
        }
        commandRegistrar.bind(handler);
    }
}
