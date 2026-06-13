package dev.rono.igniscore.paper.adapter;

import dev.rono.igniscore.command.PluginCommandHandler;
import dev.rono.igniscore.paper.command.PaperBasicCommandAdapter;
import dev.rono.igniscore.platform.paper.PaperPlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class PaperPlatformAdapter extends BukkitPlatformAdapter {

    public PaperPlatformAdapter(JavaPlugin plugin) {
        super(plugin, new PaperPlatformHooks());
    }

    @Override
    public void registerCommand(String name, Object commandExecutor) {
        if (!(commandExecutor instanceof PluginCommandHandler handler)) {
            return;
        }

        Map<String, Map<String, Object>> commands = plugin().getDescription().getCommands();
        Map<String, Object> metadata = commands != null ? commands.get(name) : null;

        String description = metadata != null ? String.valueOf(metadata.getOrDefault("description", "")) : "";
        String permission = metadata != null ? String.valueOf(metadata.getOrDefault("permission", "")) : "";

        plugin().registerCommand(
                name,
                description,
                readAliases(metadata),
                new PaperBasicCommandAdapter(handler, name, permission.isBlank() ? null : permission));
    }

    private static List<String> readAliases(Map<String, Object> metadata) {
        if (metadata == null) {
            return List.of();
        }
        Object aliases = metadata.get("aliases");
        if (aliases instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        if (aliases instanceof String singleAlias) {
            return List.of(singleAlias);
        }
        return List.of();
    }
}
