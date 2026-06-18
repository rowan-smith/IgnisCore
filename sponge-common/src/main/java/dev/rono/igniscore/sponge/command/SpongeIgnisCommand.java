package dev.rono.igniscore.sponge.command;

import com.google.inject.Inject;
import dev.rono.igniscore.core.ExtensionReloadScope;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.config.SpongeIgnisConfig;
import dev.rono.igniscore.sponge.resourcepack.SpongeResourcePackService;
import dev.rono.igniscore.sponge.service.SpongeBlockItemFactory;
import dev.rono.igniscore.sponge.service.SpongeExtensionReloadService;
import dev.rono.igniscore.sponge.service.SpongeItemFactory;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

public class SpongeIgnisCommand {
    private final SpongePluginContext pluginContext;
    private final BlockManager blockManager;
    private final ItemManager itemManager;
    private final SpongeResourcePackService resourcePackService;
    private final SpongeExtensionReloadService extensionReloadService;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final SpongeBlockItemFactory blockItemFactory;
    private final SpongeItemFactory itemFactory;
    private final SpongeIgnisConfig config;

    @Inject
    public SpongeIgnisCommand(SpongePluginContext pluginContext,
                              BlockManager blockManager,
                              ItemManager itemManager,
                              SpongeResourcePackService resourcePackService,
                              SpongeExtensionReloadService extensionReloadService,
                              BlockExtensionLoader blockExtensionLoader,
                              ItemExtensionLoader itemExtensionLoader,
                              SpongeBlockItemFactory blockItemFactory,
                              SpongeItemFactory itemFactory,
                              SpongeIgnisConfig config) {
        this.pluginContext = pluginContext;
        this.blockManager = blockManager;
        this.itemManager = itemManager;
        this.resourcePackService = resourcePackService;
        this.extensionReloadService = extensionReloadService;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.blockItemFactory = blockItemFactory;
        this.itemFactory = itemFactory;
        this.config = config;
    }

    public Command.Parameterized build() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::executeRoot)
                .addChild(reloadCommand(), "reload")
                .addChild(blocksCommand(), "blocks")
                .addChild(itemsCommand(), "items")
                .addChild(giveCommand(), "give")
                .addChild(packCommand(), "pack")
                .addChild(debugCommand(), "debug")
                .build();
    }

    private Command.Parameterized reloadCommand() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::executeReload)
                .addChild(Command.builder().executor(ctx -> executeReloadTarget(ctx, "all")).build(), "all")
                .addChild(Command.builder().executor(ctx -> executeReloadTarget(ctx, "blocks")).build(), "blocks")
                .addChild(Command.builder().executor(ctx -> executeReloadTarget(ctx, "items")).build(), "items")
                .addChild(Command.builder().executor(ctx -> executeReloadTarget(ctx, "server")).build(), "server")
                .build();
    }

    private Command.Parameterized blocksCommand() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::handleBlocks)
                .build();
    }

    private Command.Parameterized itemsCommand() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::handleItems)
                .build();
    }

    private Command.Parameterized giveCommand() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::handleGive)
                .build();
    }

    private Command.Parameterized packCommand() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::handlePack)
                .build();
    }

    private Command.Parameterized debugCommand() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::handleDebugRoot)
                .addChild(Command.builder().executor(ctx -> handleDebug(ctx, "on")).build(), "on")
                .addChild(Command.builder().executor(ctx -> handleDebug(ctx, "off")).build(), "off")
                .addChild(Command.builder().executor(ctx -> handleDebug(ctx, "pack")).build(), "pack")
                .build();
    }

    private CommandResult executeRoot(CommandContext context) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        sendHelp(context.subject());
        return CommandResult.success();
    }

    private CommandResult executeReload(CommandContext context) {
        return executeReloadTarget(context, "all");
    }

    private CommandResult executeReloadTarget(CommandContext context, String target) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        Subject sender = context.subject();
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
                return CommandResult.success();
            }
        }
        return CommandResult.success();
    }

    private CommandResult handleBlocks(CommandContext context) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        Subject subject = context.subject();
        send(subject, "<gold>Loaded Block Extensions:");
        if (blockExtensionLoader.getLoadedExtensions().isEmpty()) {
            send(subject, "<gray>None");
            return CommandResult.success();
        }
        blockExtensionLoader.getLoadedExtensions().forEach(extension ->
                send(subject, "<gray>- <white>" + extension.getManifest().getName()
                        + " <dark_gray>v" + extension.getManifest().getVersion()
                        + " -> block <white>" + extension.getDefinition().getId()));
        return CommandResult.success();
    }

    private CommandResult handleItems(CommandContext context) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        Subject subject = context.subject();
        send(subject, "<gold>Loaded Item Extensions:");
        if (itemExtensionLoader.getLoadedExtensions().isEmpty()) {
            send(subject, "<gray>None");
            return CommandResult.success();
        }
        itemExtensionLoader.getLoadedExtensions().forEach(extension ->
                send(subject, "<gray>- <white>" + extension.getManifest().getName()
                        + " <dark_gray>v" + extension.getManifest().getVersion()
                        + " -> item <white>" + extension.getDefinition().getId()));
        return CommandResult.success();
    }

    private CommandResult handleGive(CommandContext context) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        String[] args = commandArgs(context);
        if (args.length < 4 || !"give".equalsIgnoreCase(args[0])) {
            send(context.subject(), "<red>Usage: /ignis give <player> <block|item> <id>");
            return CommandResult.success();
        }

        ServerPlayer target = SpongeRuntimeHolder.server().player(args[1]).orElse(null);
        if (target == null) {
            send(context.subject(), "<red>Player not found.");
            return CommandResult.success();
        }

        return switch (args[2].toLowerCase()) {
            case "block" -> giveBlock(context.subject(), target, args[3]);
            case "item" -> giveItem(context.subject(), target, args[3]);
            default -> {
                send(context.subject(), "<red>Usage: /ignis give <player> <block|item> <id>");
                yield CommandResult.success();
            }
        };
    }

    private CommandResult giveBlock(Subject sender, ServerPlayer target, String typeId) {
        if (!blockManager.getBlockTypes().containsKey(typeId)) {
            send(sender, "<red>Unknown block type: " + typeId);
            return CommandResult.success();
        }
        target.inventory().offer(blockItemFactory.createBlockItem(typeId));
        send(sender, "<green>Gave block " + typeId + " to " + target.name());
        return CommandResult.success();
    }

    private CommandResult giveItem(Subject sender, ServerPlayer target, String typeId) {
        if (!itemManager.getItemTypes().containsKey(typeId)) {
            send(sender, "<red>Unknown item type: " + typeId);
            return CommandResult.success();
        }
        target.inventory().offer(itemFactory.createItem(typeId));
        send(sender, "<green>Gave item " + typeId + " to " + target.name());
        return CommandResult.success();
    }

    private CommandResult handlePack(CommandContext context) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        if (!(context.subject() instanceof ServerPlayer player)) {
            send(context.subject(), "<red>Only players can use this.");
            return CommandResult.success();
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
        return CommandResult.success();
    }

    private CommandResult handleDebugRoot(CommandContext context) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        send(context.subject(), "<red>Usage: /ignis debug <on|off|pack>");
        return CommandResult.success();
    }

    private CommandResult handleDebug(CommandContext context, String mode) {
        if (!checkPermission(context)) {
            return CommandResult.success();
        }
        return switch (mode) {
            case "on" -> {
                pluginContext.setDebugEnabled(true);
                send(context.subject(), "<green>Debug mode enabled.");
                yield CommandResult.success();
            }
            case "off" -> {
                pluginContext.setDebugEnabled(false);
                send(context.subject(), "<red>Debug mode disabled.");
                yield CommandResult.success();
            }
            case "pack" -> {
                sendPackDebug(context.subject());
                yield CommandResult.success();
            }
            default -> CommandResult.success();
        };
    }

    private void sendPackDebug(Subject sender) {
        send(sender, "<gold>IgnisCore Debug Pack:");
        send(sender, "<yellow>Latest Hash: <white>" + resourcePackService.getLatestHash());
        send(sender, "<yellow>Registered Blocks:");
        for (BlockDefinition definition : blockManager.getBlockTypes().values()) {
            send(sender, "<gray>- <white>" + definition.getId());
            send(sender, "<gray>  Inventory: <white>" + definition.getBaseMaterial()
                    + " (CMD " + definition.getCustomModelData() + ") -> igniscore:item/" + definition.getId());
            send(sender, "<gray>  Extension: <white>" + definition.getExtensionId());
        }
        send(sender, "<yellow>Public URL: <white>" + config.resourcePackPublicUrl());
    }

    private boolean checkPermission(CommandContext context) {
        Subject subject = context.subject();
        if (!subject.hasPermission("igniscore.admin")) {
            send(subject, "<red>You do not have permission to use this command.");
            return false;
        }
        return true;
    }

    private void sendHelp(Subject subject) {
        send(subject, "<gold>IgnisCore Commands:");
        send(subject, "<yellow>/ignis give <player> <block|item> <id>");
        send(subject, "<yellow>/ignis pack - Apply resource pack");
        send(subject, "<yellow>/ignis reload <all|blocks|items|server>");
        send(subject, "<yellow>/ignis blocks - List loaded block JARs");
        send(subject, "<yellow>/ignis items - List loaded item JARs");
        send(subject, "<yellow>/ignis debug <on|off|pack>");
    }

    private void send(Subject subject, String miniMessage) {
        Component message = pluginContext.message(miniMessage);
        if (subject instanceof ServerPlayer player) {
            player.sendMessage(message);
        } else if (subject instanceof net.kyori.adventure.audience.Audience audience) {
            audience.sendMessage(message);
        }
    }

    private static String[] commandArgs(CommandContext context) {
        String input = context.cause().context().get(org.spongepowered.api.event.EventContextKeys.COMMAND)
                .orElse("")
                .replaceFirst("^ignis\\s+", "")
                .trim();
        return input.isBlank() ? new String[0] : input.split("\\s+");
    }
}
