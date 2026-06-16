package dev.rono.igniscore.paper.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class PaperIgnisCommandRegistrar {
    private final AtomicReference<PluginCommandHandler> handler = new AtomicReference<>();

    public void install(JavaPlugin plugin) {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Set<String> labels = event.registrar().register(
                    buildNode(IgnisCommands.IGNIS),
                    IgnisCommands.DESCRIPTION,
                    IgnisCommands.ALIASES);
            plugin.getLogger().info("Registered IgnisCore Paper commands: " + String.join(", ", labels));
        });
    }

    public void bind(PluginCommandHandler commandHandler) {
        handler.set(commandHandler);
    }

    private com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> buildNode(String label) {
        return Commands.literal(label)
                .executes(ctx -> execute(ctx, label, new String[0]))
                .then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String raw = StringArgumentType.getString(ctx, "args");
                            String[] args = raw.isBlank() ? new String[0] : raw.split(" ");
                            return execute(ctx, label, args);
                        }))
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> context, String label, String[] args) {
        CommandSender sender = context.getSource().getSender();
        PluginCommandHandler current = handler.get();
        if (current == null) {
            sender.sendMessage("IgnisCore is still starting up. Try again in a moment.");
            return 0;
        }
        if (!sender.hasPermission(IgnisCommands.PERMISSION)) {
            sender.sendMessage("You do not have permission to use this command.");
            return 0;
        }
        current.onCommand(sender, noopCommand(label), label, args);
        return Command.SINGLE_SUCCESS;
    }

    private static org.bukkit.command.Command noopCommand(String label) {
        return new org.bukkit.command.Command(label) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return false;
            }
        };
    }
}
