package dev.rono.igniscore.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.rono.igniscore.command.IgnisCommandBridge;
import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.IgnisCommandSupport;
import dev.rono.igniscore.command.BukkitIgnisCommandSupport;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public final class IgnisBrigadierTree {
    private IgnisBrigadierTree() {
    }

    public static com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> build(IgnisCommandBridge bridge) {
        return root(bridge, IgnisCommands.IGNIS).build();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> root(IgnisCommandBridge bridge, String label) {
        return Commands.literal(label)
                .executes(ctx -> execute(bridge, ctx, label))
                .then(Commands.literal("give")
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests((ctx, builder) -> suggest(bridge, ctx, label, "give", builder))
                                .then(Commands.literal("block")
                                        .then(Commands.argument("id", StringArgumentType.word())
                                                .suggests((ctx, builder) -> suggest(bridge, ctx, label, "give", builder))
                                                .executes(ctx -> execute(bridge, ctx, label, "give",
                                                        StringArgumentType.getString(ctx, "player"),
                                                        "block",
                                                        StringArgumentType.getString(ctx, "id")))))
                                .then(Commands.literal("item")
                                        .then(Commands.argument("id", StringArgumentType.word())
                                                .suggests((ctx, builder) -> suggest(bridge, ctx, label, "give", builder))
                                                .executes(ctx -> execute(bridge, ctx, label, "give",
                                                        StringArgumentType.getString(ctx, "player"),
                                                        "item",
                                                        StringArgumentType.getString(ctx, "id")))))))
                .then(Commands.literal("pack").executes(ctx -> execute(bridge, ctx, label, "pack")))
                .then(Commands.literal("reload")
                        .executes(ctx -> execute(bridge, ctx, label, "reload"))
                        .then(Commands.literal("all").executes(ctx -> execute(bridge, ctx, label, "reload", "all")))
                        .then(Commands.literal("blocks").executes(ctx -> execute(bridge, ctx, label, "reload", "blocks")))
                        .then(Commands.literal("items").executes(ctx -> execute(bridge, ctx, label, "reload", "items")))
                        .then(Commands.literal("server").executes(ctx -> execute(bridge, ctx, label, "reload", "server"))))
                .then(Commands.literal("debug")
                        .then(Commands.literal("on").executes(ctx -> execute(bridge, ctx, label, "debug", "on")))
                        .then(Commands.literal("off").executes(ctx -> execute(bridge, ctx, label, "debug", "off")))
                        .then(Commands.literal("pack").executes(ctx -> execute(bridge, ctx, label, "debug", "pack"))))
                .then(Commands.literal("blocks").executes(ctx -> execute(bridge, ctx, label, "blocks")))
                .then(Commands.literal("items").executes(ctx -> execute(bridge, ctx, label, "items")));
    }

    private static int execute(IgnisCommandBridge bridge,
                               CommandContext<CommandSourceStack> context,
                               String label,
                               String... args) {
        CommandSender sender = context.getSource().getSender();
        if (!BukkitIgnisCommandSupport.hasPermission(sender)) {
            BukkitIgnisCommandSupport.sendPlain(sender, "You do not have permission to use this command.");
            return 0;
        }
        bridge.execute(sender, label, args);
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggest(IgnisCommandBridge bridge,
                                                                                          CommandContext<CommandSourceStack> context,
                                                                                          String label,
                                                                                          String rootArg,
                                                                                          SuggestionsBuilder builder) {
        CommandSender sender = context.getSource().getSender();
        String remaining = builder.getRemaining();
        String input = rootArg + (remaining.isBlank() ? "" : " " + remaining);
        String[] args = IgnisCommandSupport.splitArgs(input);
        return bridge.suggest(sender, label, args, builder);
    }
}
