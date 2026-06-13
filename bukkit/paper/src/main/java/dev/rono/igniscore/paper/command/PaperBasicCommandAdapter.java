package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.PluginCommandHandler;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class PaperBasicCommandAdapter implements BasicCommand {
    private static final Command NO_OP_COMMAND = new Command("ignis") {
        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            return List.of();
        }
    };

    private final AtomicReference<PluginCommandHandler> handler = new AtomicReference<>();
    private final String label;
    private final String permission;

    public PaperBasicCommandAdapter(String label, String permission) {
        this.label = label;
        this.permission = permission;
    }

    public void bind(PluginCommandHandler handler) {
        this.handler.set(handler);
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        requireHandler().onCommand(stack.getSender(), NO_OP_COMMAND, label, args);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        return requireHandler().onTabComplete(stack.getSender(), NO_OP_COMMAND, label, args);
    }

    @Override
    public String permission() {
        return permission;
    }

    private PluginCommandHandler requireHandler() {
        PluginCommandHandler current = handler.get();
        if (current == null) {
            throw new IllegalStateException("IgnisCore command handler is not ready yet");
        }
        return current;
    }
}
