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
import dev.rono.igniscore.platform.PlatformHooks;
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
    private static final List<String> RELOAD_SUBCOMMANDS = List.of("all", "blocks", "items", "server");
    private static final List<String> GIVE_TYPE_SUBCOMMANDS = List.of("block", "item");

    private final Main plugin;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final ResourcePackService resourcePackService;
    private final ExtensionBootstrap extensionBootstrap;
    private final IgnisStrategyRegistry strategyRegistry;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final ItemFactory itemFactory;
    private final PlatformHooks platformHooks;

    @Inject
    public IgnisCommand(Main plugin,
                        BlockManager blockManager,
                        ItemManager itemManager,
                        ResourcePackService resourcePackService,
                        ExtensionBootstrap extensionBootstrap,
                        IgnisStrategyRegistry strategyRegistry,
                        BlockExtensionLoader blockExtensionLoader,
                        ItemExtensionLoader itemExtensionLoader,
                        ItemFactory itemFactory,
                        PlatformHooks platformHooks) {
        this.plugin = plugin;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.resourcePackService = resourcePackService;
        this.extensionBootstrap = extensionBootstrap;
        this.strategyRegistry = strategyRegistry;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.itemFactory = itemFactory;
        this.platformHooks = platformHooks;
    }

    private void send(CommandSender sender, String message) {
        platformHooks.sendMessage(sender, plugin.message(message));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("igniscore.admin")) {
            send(sender, "<red>You do not have permission to use this command.");
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
        if (args.length < 4) {
            send(sender, "<red>Usage: /ignis give <player> <block|item> <id>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            send(sender, "<red>Player not found.");
            return true;
        }

        String kind = args[2].toLowerCase();
        String typeId = args[3];

        return switch (kind) {
            case "block" -> giveBlock(sender, target, typeId);
            case "item" -> giveItem(sender, target, typeId);
            default -> {
                send(sender, "<red>Usage: /ignis give <player> <block|item> <id>");
                yield true;
            }
        };
    }

    private boolean giveBlock(CommandSender sender, Player target, String typeId) {
        if (!blockManager.getBlockTypes().containsKey(typeId)) {
            send(sender, "<red>Unknown block type: " + typeId);
            return true;
        }

        target.getInventory().addItem(plugin.createBlockItem(typeId));
        send(sender, "<green>Gave block " + typeId + " to " + target.getName());
        return true;
    }

    private boolean giveItem(CommandSender sender, Player target, String typeId) {
        if (!itemManager.getItemTypes().containsKey(typeId)) {
            send(sender, "<red>Unknown item type: " + typeId);
            return true;
        }

        target.getInventory().addItem(itemFactory.createItem(typeId));
        send(sender, "<green>Gave item " + typeId + " to " + target.getName());
        return true;
    }

    private boolean handlePack(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            send(sender, "<red>Only players can use this.");
            return true;
        }

        try {
            resourcePackService.buildAndRegister();
            plugin.getLogger().info("Resource pack rebuilt successfully! Hash: " + resourcePackService.getLatestHash());
            send(player, "<green>Resource pack rebuilt. Reconnect if models do not update.");
        } catch (IOException e) {
            send(player, "<red>Failed to rebuild resource pack: " + e.getMessage());
            return true;
        }

        resourcePackService.requestPack(player);
        return true;
    }

    private boolean handleReload(CommandSender sender, String[] args) {
        String target = args.length > 1 ? args[1].toLowerCase() : "all";
        try {
            switch (target) {
                case "server" -> {
                    resourcePackService.reloadConfiguration();
                    send(sender, "<green>IgnisCore server configuration reloaded.");
                }
                case "all" -> {
                    extensionBootstrap.reloadAll();
                    resourcePackService.buildAndRegister();
                    resourcePackService.reloadConfiguration();
                    send(sender, "<green>IgnisCore fully reloaded.");
                }
                case "blocks" -> {
                    extensionBootstrap.reloadBlocks();
                    resourcePackService.buildAndRegister();
                    send(sender, "<green>IgnisCore block extensions reloaded.");
                }
                case "items" -> {
                    extensionBootstrap.reloadItems();
                    resourcePackService.buildAndRegister();
                    send(sender, "<green>IgnisCore item extensions reloaded.");
                }
                default -> {
                    send(sender, "<red>Usage: /ignis reload <all|blocks|items|server>");
                    return true;
                }
            }
        } catch (IOException e) {
            send(sender, "<red>Reload failed: " + e.getMessage());
        }
        return true;
    }

    private boolean handleBlocks(CommandSender sender) {
        send(sender, "<gold>Loaded Block Extensions:");
        if (blockExtensionLoader.getLoadedExtensions().isEmpty()) {
            send(sender, "<gray>None");
            return true;
        }

        for (LoadedExtension<BlockDefinition> extension : blockExtensionLoader.getLoadedExtensions()) {
            BlockDefinition definition = extension.getDefinition();
            send(sender, "<gray>- <white>" + extension.getManifest().getName()
                    + " <dark_gray>v" + extension.getManifest().getVersion()
                    + " -> block <white>" + definition.getId()
                    + " <dark_gray>(strategy: " + definition.getStrategy() + ")</dark_gray>");
        }

        send(sender, "<gold>Registered Strategies:");
        for (IgnisStrategyDescriptor descriptor : strategyRegistry.getDescriptors()) {
            send(sender, "<gray>- <white>" + descriptor.getId()
                    + " <dark_gray>from " + descriptor.getSourcePlugin() + "</dark_gray>");
        }
        return true;
    }

    private boolean handleItems(CommandSender sender) {
        send(sender, "<gold>Loaded Item Extensions:");
        if (itemExtensionLoader.getLoadedExtensions().isEmpty()) {
            send(sender, "<gray>None");
            return true;
        }

        for (LoadedExtension<ItemDefinition> extension : itemExtensionLoader.getLoadedExtensions()) {
            send(sender, "<gray>- <white>" + extension.getManifest().getName()
                    + " <dark_gray>v" + extension.getManifest().getVersion()
                    + " -> item <white>" + extension.getDefinition().getId()
                    + " <dark_gray>(strategy: " + extension.getDefinition().getStrategy() + ")</dark_gray>");
        }
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (args.length < 2) {
            send(sender, "<red>Usage: /ignis debug <on|off|pack>");
            return true;
        }

        return switch (args[1].toLowerCase()) {
            case "on" -> {
                plugin.setDebugEnabled(true);
                send(sender, "<green>Debug mode enabled.");
                yield true;
            }
            case "off" -> {
                plugin.setDebugEnabled(false);
                send(sender, "<red>Debug mode disabled.");
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
        send(sender, "<gold>IgnisCore Commands:");
        send(sender, "<yellow>/ignis give <player> <block|item> <id>");
        send(sender, "<yellow>/ignis pack - Apply resource pack");
        send(sender, "<yellow>/ignis reload <all|blocks|items|server>");
        send(sender, "<yellow>/ignis blocks - List loaded block JARs");
        send(sender, "<yellow>/ignis items - List loaded item JARs");
    }

    private void sendPackDebug(CommandSender sender) {
        send(sender, "<gold>IgnisCore Debug Pack:");
        send(sender, "<yellow>Latest Hash: <white>" + resourcePackService.getLatestHash());
        send(sender, "<yellow>Registered Blocks:");

        for (BlockDefinition def : blockManager.getBlockTypes().values()) {
            send(sender, "<gray>- <white>" + def.getId());
            send(sender, "<gray>  Inventory: <white>" + def.getBaseMaterial()
                    + " (CMD " + def.getCustomModelData() + ") -> igniscore:item/" + def.getId());
            send(sender, "<gray>  Extension: <white>" + def.getExtensionId()
                    + " strategy: " + def.getStrategy());
        }

        String url = plugin.getConfig().getString("resource-pack.public-url");
        send(sender, "<yellow>Public URL: <white>" + url);
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
            completions.addAll(GIVE_TYPE_SUBCOMMANDS);
        } else if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            if (args[2].equalsIgnoreCase("block")) {
                completions.addAll(blockManager.getBlockTypes().keySet());
            } else if (args[2].equalsIgnoreCase("item")) {
                completions.addAll(itemManager.getItemTypes().keySet());
            }
        }

        String lastArg = args[args.length - 1].toLowerCase();
        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(lastArg))
                .toList();
    }
}
