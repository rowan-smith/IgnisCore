package dev.rono.igniscore.command;

import org.bukkit.command.CommandSender;

public final class IgnisCommandSupport {
    private IgnisCommandSupport() {
    }

    public static boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(IgnisCommands.PERMISSION);
    }

    public static void sendPlain(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public static String[] splitArgs(String raw) {
        if (raw == null || raw.isBlank()) {
            return new String[0];
        }
        return raw.trim().split("\\s+");
    }
}
