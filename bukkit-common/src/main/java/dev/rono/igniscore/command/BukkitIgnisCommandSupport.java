package dev.rono.igniscore.command;

import org.bukkit.command.CommandSender;

public final class BukkitIgnisCommandSupport {
    private BukkitIgnisCommandSupport() {
    }

    public static boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(IgnisCommands.PERMISSION);
    }

    public static void sendPlain(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
}
