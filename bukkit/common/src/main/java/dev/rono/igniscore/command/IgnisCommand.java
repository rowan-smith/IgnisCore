package dev.rono.igniscore.command;

import com.google.inject.Inject;
import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.core.ExtensionReloadScope;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.ExtensionReloadService;
import dev.rono.igniscore.service.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IgnisCommand implements PluginCommandHandler {
    private static final List<String> ROOT_SUBCOMMANDS = List.of("give", "pack", "reload", "debug", "blocks", "items");
    private static final List<String> DEBUG_SUBCOMMANDS = List.of("on", "off", "pack");
    private static final List<String> RELOAD_SUBCOMMANDS = List.of("all", "blocks", "items", "server");
    private static final List<String> GIVE_TYPE_SUBCOMMANDS = List.of("block", "item");

    private final IgnisPluginContext pluginContext;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final ResourcePackService resourcePackService;
    private final ExtensionReloadService extensionReloadService;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final BlockItemFactory blockItemFactory;
    private final ItemFactory itemFactory;
    private final PlatformHooks platformHooks;

    @Inject
    public IgnisCommand(IgnisPluginContext pluginContext,
                        BlockManager blockManager,
                        ItemManager itemManager,
                        ResourcePackService resourcePackService,
                        ExtensionReloadService extensionReloadService,
                        BlockExtensionLoader blockExtensionLoader,
                        ItemExtensionLoader itemExtensionLoader,
                        BlockItemFactory blockItemFactory,
                        ItemFactory itemFactory,
                        PlatformHooks platformHooks) {
        this.pluginContext = pluginContext;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.resourcePackService = resourcePackService;
        this.extensionReloadService = extensionReloadService;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.blockItemFactory = blockItemFactory;
        this.itemFactory = itemFactory;
        this.platformHooks = platformHooks;
    }

    private void send(CommandSender sender, String message) {
        platformHooks.sendMessage(sender, pluginContext.message(message));
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

        target.getInventory().addItem(blockItemFactory.createBlockItem(typeId));
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

        send(player, "<yellow>Rebuilding resource pack...");
        resourcePackService.buildAndRegisterAsync(
                () -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    pluginContext.debug("Resource pack rebuilt successfully! Hash: " + resourcePackService.getLatestHash());
                    send(player, "<green>Resource pack rebuilt. Reconnect if models do not update.");
                    resourcePackService.requestPack(player);
                },
                error -> {
                    if (player.isOnline()) {
                        send(player, "<red>Failed to rebuild resource pack: " + error.getMessage());
                    }
                });
        return true;
    }

    private boolean handleReload(CommandSender sender, String[] args) {
        String target = args.length > 1 ? args[1].toLowerCase() : "all";
        switch (target) {
            case "server" -> {
                resourcePackService.reloadConfiguration();
                send(sender, "<green>IgnisCore server configuration reloaded.");
            }
            case "all" -> extensionReloadService.reloadAsync(
                    ExtensionReloadScope.ALL,
                    sender,
                    "<yellow>Reloading extensions...",
                    "<yellow>Extensions reloaded. Rebuilding resource pack...",
                    () -> resourcePackService.buildAndRegisterAsync(
                            () -> {
                                resourcePackService.reloadConfiguration();
                                send(sender, "<green>IgnisCore fully reloaded.");
                            },
                            error -> send(sender, "<red>Reload failed: " + error.getMessage())));
            case "blocks" -> extensionReloadService.reloadAsync(
                    ExtensionReloadScope.BLOCKS,
                    sender,
                    "<yellow>Reloading block extensions...",
                    "<yellow>Block extensions reloaded. Rebuilding resource pack...",
                    () -> resourcePackService.buildAndRegisterAsync(
                            () -> send(sender, "<green>IgnisCore block extensions reloaded."),
                            error -> send(sender, "<red>Reload failed: " + error.getMessage())));
            case "items" -> extensionReloadService.reloadAsync(
                    ExtensionReloadScope.ITEMS,
                    sender,
                    "<yellow>Reloading item extensions...",
                    "<yellow>Item extensions reloaded. Rebuilding resource pack...",
                    () -> resourcePackService.buildAndRegisterAsync(
                            () -> send(sender, "<green>IgnisCore item extensions reloaded."),
                            error -> send(sender, "<red>Reload failed: " + error.getMessage())));
            default -> {
                send(sender, "<red>Usage: /ignis reload <all|blocks|items|server>");
                return true;
            }
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
                    + " -> block <white>" + definition.getId() + "</dark_gray>");
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
                    + " -> item <white>" + extension.getDefinition().getId() + "</dark_gray>");
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
                pluginContext.setDebugEnabled(true);
                send(sender, "<green>Debug mode enabled.");
                yield true;
            }
            case "off" -> {
                pluginContext.setDebugEnabled(false);
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
            send(sender, "<gray>  Extension: <white>" + def.getExtensionId());
        }

        String url = pluginContext.plugin().getConfig().getString("resource-pack.public-url");
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
            if (args[2].equalsIgnoreCase("block")) {
                completions.addAll(blockManager.getBlockTypes().keySet());
            } else if (args[2].equalsIgnoreCase("item")) {
                completions.addAll(itemManager.getItemTypes().keySet());
            } else {
                completions.addAll(GIVE_TYPE_SUBCOMMANDS);
            }
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
