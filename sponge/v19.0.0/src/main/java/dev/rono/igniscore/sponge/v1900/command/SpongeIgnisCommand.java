package dev.rono.igniscore.sponge.v1900.command;

import com.google.inject.Inject;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.BlockExtensionLoader;
import dev.rono.igniscore.loader.ItemExtensionLoader;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.sponge.v1900.service.SpongeItemFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;

public class SpongeIgnisCommand {
    private final ItemManager itemManager;
    private final ExtensionBootstrap extensionBootstrap;
    private final BlockExtensionLoader blockExtensionLoader;
    private final ItemExtensionLoader itemExtensionLoader;
    private final SpongeItemFactory itemFactory;

    @Inject
    public SpongeIgnisCommand(ItemManager itemManager,
                              ExtensionBootstrap extensionBootstrap,
                              BlockExtensionLoader blockExtensionLoader,
                              ItemExtensionLoader itemExtensionLoader,
                              SpongeItemFactory itemFactory) {
        this.itemManager = itemManager;
        this.extensionBootstrap = extensionBootstrap;
        this.blockExtensionLoader = blockExtensionLoader;
        this.itemExtensionLoader = itemExtensionLoader;
        this.itemFactory = itemFactory;
    }

    public Command.Parameterized build() {
        return Command.builder()
                .permission("igniscore.admin")
                .executor(this::execute)
                .build();
    }

    private CommandResult execute(CommandContext context) {
        Subject subject = context.subject();
        if (!subject.hasPermission("igniscore.admin")) {
            send(subject, "<red>You do not have permission to use this command.");
            return CommandResult.success();
        }

        String input = context.cause().context().get(org.spongepowered.api.event.EventContextKeys.COMMAND)
                .orElse("")
                .replaceFirst("^ignis\\s*", "")
                .trim();
        if (input.isEmpty()) {
            sendHelp(subject);
            return CommandResult.success();
        }

        String[] args = input.split("\\s+");
        return switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(subject, args);
            case "blocks" -> handleBlocks(subject);
            case "items" -> handleItems(subject);
            case "give" -> handleGive(subject, args);
            default -> {
                sendHelp(subject);
                yield CommandResult.success();
            }
        };
    }

    private CommandResult handleReload(Subject subject, String[] args) {
        String target = args.length > 1 ? args[1].toLowerCase() : "all";
        switch (target) {
            case "all" -> extensionBootstrap.reloadAll();
            case "blocks" -> extensionBootstrap.reloadBlocks();
            case "items" -> extensionBootstrap.reloadItems();
            default -> {
                send(subject, "<red>Usage: /ignis reload <all|blocks|items>");
                return CommandResult.success();
            }
        }
        send(subject, "<green>IgnisCore reloaded (" + target + ").");
        return CommandResult.success();
    }

    private CommandResult handleBlocks(Subject subject) {
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

    private CommandResult handleItems(Subject subject) {
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

    private CommandResult handleGive(Subject subject, String[] args) {
        if (args.length < 4) {
            send(subject, "<red>Usage: /ignis give <player> item <id>");
            return CommandResult.success();
        }

        var server = org.spongepowered.api.Sponge.server();
        var target = server.player(args[1]).orElse(null);
        if (target == null) {
            send(subject, "<red>Player not found.");
            return CommandResult.success();
        }

        if (!"item".equalsIgnoreCase(args[2])) {
            send(subject, "<red>Usage: /ignis give <player> item <id>");
            return CommandResult.success();
        }

        String typeId = args[3];
        if (!itemManager.getItemTypes().containsKey(typeId)) {
            send(subject, "<red>Unknown item type: " + typeId);
            return CommandResult.success();
        }

        target.inventory().offer(itemFactory.createItem(typeId));
        send(subject, "<green>Gave item " + typeId + " to " + target.name());
        return CommandResult.success();
    }

    private void sendHelp(Subject subject) {
        send(subject, "<gold>IgnisCore Commands:");
        send(subject, "<yellow>/ignis give <player> item <id>");
        send(subject, "<yellow>/ignis reload <all|blocks|items>");
        send(subject, "<yellow>/ignis blocks");
        send(subject, "<yellow>/ignis items");
    }

    private static void send(Subject subject, String miniMessage) {
        Component message = MiniMessage.miniMessage().deserialize(miniMessage);
        if (subject instanceof ServerPlayer player) {
            player.sendMessage(message);
        } else if (subject instanceof net.kyori.adventure.audience.Audience audience) {
            audience.sendMessage(message);
        }
    }
}
