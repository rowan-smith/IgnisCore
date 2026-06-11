package dev.rono.igniscore.command;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.service.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IgnisCommand implements PluginCommandHandler {
    private static final List<String> ROOT_SUBCOMMANDS = List.of("give", "pack", "reload", "debug", "blocks", "items");
    private static final List<String> DEBUG_SUBCOMMANDS = List.of("on", "off", "pack");
    private static final List<String> RELOAD_SUBCOMMANDS = List.of("all", "blocks", "items");

    private final Main plugin;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final ResourcePackService resourcePackService;
    private final ExtensionBootstrap extensionBootstrap;
    private final IgnisStrategyRegistry strategyRegistry;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final ItemFactory itemFactory;

    @Inject
    public IgnisCommand(Main plugin,
                        BlockManager blockManager,
                        ItemManager itemManager,
                        ResourcePackService resourcePackService,
                        ExtensionBootstrap extensionBootstrap,
                        IgnisStrategyRegistry strategyRegistry,
                        BlockExtensionLoader blockExtensionLoader,
                        ItemExtensionLoader itemExtensionLoader,
                        ItemFactory itemFactory) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.resourcePackService = resourcePackService;
        this.extensionBootstrap = extensionBootstrap;
        this.strategyRegistry = strategyRegistry;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.itemFactory = itemFactory;
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
            case "reload" -> handleReload(sender, args);
            case "debug" -> handleDebug(sender, args);
            case "blocks" -> handleBlocks(sender);
            case "items" -> handleItems(sender);
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
        if (blockManager.getBlockTypes().containsKey(typeId)) {
            target.getInventory().addItem(plugin.createBlockItem(typeId));
            sender.sendMessage(plugin.message("<green>Gave block " + typeId + " to " + target.getName()));
            return true;
        }

        if (itemManager.getItemTypes().containsKey(typeId)) {
            target.getInventory().addItem(itemFactory.createItem(typeId));
            sender.sendMessage(plugin.message("<green>Gave item " + typeId + " to " + target.getName()));
            return true;
        }

        sender.sendMessage(plugin.message("<red>Unknown block or item type."));
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

    private boolean handleReload(CommandSender sender, String[] args) {
        String target = args.length > 1 ? args[1].toLowerCase() : "all";
        try {
            switch (target) {
                case "all" -> extensionBootstrap.reloadAll();
                case "blocks" -> extensionBootstrap.reloadBlocks();
                case "items" -> extensionBootstrap.reloadItems();
                default -> {
                    sender.sendMessage(plugin.message("<red>Usage: /ignis reload <all|blocks|items>"));
                    return true;
                }
            }
            resourcePackService.buildAndRegister();
            sender.sendMessage(plugin.message("<green>IgnisCore " + target + " extensions reloaded."));
        } catch (IOException e) {
            sender.sendMessage(plugin.message("<red>Extensions reloaded but resource pack rebuild failed."));
        }
        return true;
    }

    private boolean handleBlocks(CommandSender sender) {
        sender.sendMessage(plugin.message("<gold>Loaded Block Extensions:"));
        if (blockExtensionLoader.getLoadedExtensions().isEmpty()) {
            sender.sendMessage(plugin.message("<gray>None"));
            return true;
        }

        for (LoadedExtension<BlockDefinition> extension : blockExtensionLoader.getLoadedExtensions()) {
            BlockDefinition definition = extension.getDefinition();
            sender.sendMessage(plugin.message("<gray>- <white>" + extension.getManifest().getName()
                    + " <dark_gray>v" + extension.getManifest().getVersion()
                    + " -> block <white>" + definition.getId()
                    + " <dark_gray>(strategy: " + definition.getStrategy() + ")</dark_gray>"));
        }

        sender.sendMessage(plugin.message("<gold>Registered Strategies:"));
        for (IgnisStrategyDescriptor descriptor : strategyRegistry.getDescriptors()) {
            sender.sendMessage(plugin.message("<gray>- <white>" + descriptor.getId()
                    + " <dark_gray>from " + descriptor.getSourcePlugin() + "</dark_gray>"));
        }
        return true;
    }

    private boolean handleItems(CommandSender sender) {
        sender.sendMessage(plugin.message("<gold>Loaded Item Extensions:"));
        if (itemExtensionLoader.getLoadedExtensions().isEmpty()) {
            sender.sendMessage(plugin.message("<gray>None"));
            return true;
        }

        for (LoadedExtension<ItemDefinition> extension : itemExtensionLoader.getLoadedExtensions()) {
            sender.sendMessage(plugin.message("<gray>- <white>" + extension.getManifest().getName()
                    + " <dark_gray>v" + extension.getManifest().getVersion()
                    + " -> item <white>" + extension.getDefinition().getId()
                    + " <dark_gray>(strategy: " + extension.getDefinition().getStrategy() + ")</dark_gray>"));
        }
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
        sender.sendMessage(plugin.message("<yellow>/ignis reload <all|blocks|items>"));
        sender.sendMessage(plugin.message("<yellow>/ignis blocks - List loaded block JARs"));
        sender.sendMessage(plugin.message("<yellow>/ignis items - List loaded item JARs"));
    }

    private void sendPackDebug(CommandSender sender) {
        sender.sendMessage(plugin.message("<gold>IgnisCore Debug Pack:"));
        sender.sendMessage(plugin.message("<yellow>Latest Hash: <white>" + resourcePackService.getLatestHash()));
        sender.sendMessage(plugin.message("<yellow>Registered Blocks:"));

        for (BlockDefinition def : blockManager.getBlockTypes().values()) {
            sender.sendMessage(plugin.message("<gray>- <white>" + def.getId()));
            sender.sendMessage(plugin.message("<gray>  Inventory: <white>" + def.getBaseMaterial()
                    + " (CMD " + def.getCustomModelData() + ") -> igniscore:item/" + def.getId()));
            sender.sendMessage(plugin.message("<gray>  Extension: <white>" + def.getExtensionId()
                    + " strategy: " + def.getStrategy()));
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
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
            completions.addAll(RELOAD_SUBCOMMANDS);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            completions.addAll(blockManager.getBlockTypes().keySet());
            completions.addAll(itemManager.getItemTypes().keySet());
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(lastArg))
                .toList();
    }
}
