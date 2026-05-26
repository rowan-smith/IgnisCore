package dev.rono.igniscore.command;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IgnisCommand implements PluginCommandHandler {
    private static final List<String> ROOT_SUBCOMMANDS = List.of("give", "pack", "reload", "debug");
    private static final List<String> DEBUG_SUBCOMMANDS = List.of("on", "off", "pack");

    private final Main plugin;
    private final BlockManager blockManager;
    private final ResourcePackService resourcePackService;

    @Inject
    public IgnisCommand(Main plugin, BlockManager blockManager, ResourcePackService resourcePackService) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.resourcePackService = resourcePackService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("igniscore.admin")) {
            sender.sendMessage(plugin.message("<red>You do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "give" -> handleGive(sender, args);
            case "pack" -> handlePack(sender);
            case "reload" -> handleReload(sender);
            case "debug" -> handleDebug(sender, args);
            default -> false;
        };
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(plugin.message("<red>Usage: /ignis give <player> <type>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.message("<red>Player not found."));
            return true;
        }

        String typeId = args[2];
        if (!blockManager.getBlockTypes().containsKey(typeId)) {
            sender.sendMessage(plugin.message("<red>Unknown block type."));
            return true;
        }

        target.getInventory().addItem(plugin.createBlockItem(typeId));
        sender.sendMessage(plugin.message("<green>Gave " + typeId + " block to " + target.getName()));
        return true;
    }

    private boolean handlePack(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.message("<red>Only players can use this."));
            return true;
        }

        try {
            resourcePackService.reloadBuildAndRegister();
            plugin.getLogger().info("Resource pack rebuilt successfully! Hash: " + resourcePackService.getLatestHash());
            player.sendMessage(plugin.message("<green>Resource pack rebuilt. Reconnect if models do not update."));
        } catch (IOException e) {
            player.sendMessage(plugin.message("<red>Failed to rebuild resource pack: " + e.getMessage()));
            return true;
        }

        resourcePackService.requestPack(player);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        blockManager.loadConfig();
        sender.sendMessage(plugin.message("<green>IgnisCore block configs reloaded."));
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.message("<red>Usage: /ignis debug <on|off|pack>"));
            return true;
        }

        return switch (args[1].toLowerCase()) {
            case "on" -> {
                plugin.setDebugEnabled(true);
                sender.sendMessage(plugin.message("<green>Debug mode enabled."));
                yield true;
            }
            case "off" -> {
                plugin.setDebugEnabled(false);
                sender.sendMessage(plugin.message("<red>Debug mode disabled."));
                yield true;
            }
            case "pack" -> {
                sendPackDebug(sender);
                yield true;
            }
            default -> true;
        };
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.message("<gold>IgnisCore Commands:"));
        sender.sendMessage(plugin.message("<yellow>/ignis give <player> <type>"));
        sender.sendMessage(plugin.message("<yellow>/ignis pack - Apply resource pack"));
        sender.sendMessage(plugin.message("<yellow>/ignis reload - Reload block configs"));
    }

    private void sendPackDebug(CommandSender sender) {
        sender.sendMessage(plugin.message("<gold>IgnisCore Debug Pack:"));
        sender.sendMessage(plugin.message("<yellow>Latest Hash: <white>" + resourcePackService.getLatestHash()));
        sender.sendMessage(plugin.message("<yellow>Registered Blocks:"));

        for (BlockDefinition def : blockManager.getBlockTypes().values()) {
            sender.sendMessage(plugin.message("<gray>- <white>" + def.getId()));
            sender.sendMessage(plugin.message("<gray>  Inventory: <white>" + def.getBaseMaterial()
                    + " (CMD " + def.getCustomModelData() + ") -> igniscore:item/" + def.getId()));
            sender.sendMessage(plugin.message("<gray>  World Display: <white>" + def.getBaseMaterial()
                    + " (CMD " + def.getCustomModelData() + ") -> igniscore:item/" + def.getId()
                    + " -> igniscore:block/" + def.getId()));
        }

        String url = plugin.getConfig().getString("resource-pack.public-url");
        sender.sendMessage(plugin.message("<yellow>Public URL: <white>" + url));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("igniscore.admin")) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(ROOT_SUBCOMMANDS);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            completions.addAll(DEBUG_SUBCOMMANDS);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(blockManager.getBlockTypes().keySet());
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(lastArg))
                .toList();
    }
}
