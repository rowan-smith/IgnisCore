package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.PluginCommandHandler;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

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

    private final PluginCommandHandler handler;
    private final String label;
    private final String permission;

    public PaperBasicCommandAdapter(PluginCommandHandler handler, String label, String permission) {
        this.handler = handler;
        this.label = label;
        this.permission = permission;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        handler.onCommand(stack.getSender(), NO_OP_COMMAND, label, args);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack stack, String[] args) {
        return handler.onTabComplete(stack.getSender(), NO_OP_COMMAND, label, args);
    }

    @Override
    public String permission() {
        return permission;
    }
}
