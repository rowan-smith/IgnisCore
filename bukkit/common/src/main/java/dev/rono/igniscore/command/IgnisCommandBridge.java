package dev.rono.igniscore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Deferred binding target used by Brigadier and Bukkit command registrars.
 */
public final class IgnisCommandBridge {
    private final AtomicReference<PluginCommandHandler> handler = new AtomicReference<>();

    public void bind(PluginCommandHandler handler) {
        this.handler.set(handler);
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!BukkitIgnisCommandSupport.hasPermission(sender)) {
            BukkitIgnisCommandSupport.sendPlain(sender, "You do not have permission to use this command.");
            return true;
        }

        PluginCommandHandler current = handler.get();
        if (current == null) {
            BukkitIgnisCommandSupport.sendPlain(sender, "IgnisCore is still starting up. Try again in a moment.");
            return true;
        }
        return current.onCommand(sender, noopCommand(label), label, args);
    }

    public List<String> suggest(CommandSender sender, String label, String[] args) {
        PluginCommandHandler current = handler.get();
        if (current == null) {
            return List.of();
        }
        return current.onTabComplete(sender, noopCommand(label), label, args);
    }

    private static Command noopCommand(String label) {
        return new Command(label) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return false;
            }
        };
    }
}
