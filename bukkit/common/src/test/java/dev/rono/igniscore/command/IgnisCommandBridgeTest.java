package dev.rono.igniscore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisCommandBridgeTest {
    @Test
    void reportsStartupMessageWhenHandlerIsNotBound() {
        IgnisCommandBridge bridge = new IgnisCommandBridge();
        RecordingSender sender = new RecordingSender();

        assertTrue(bridge.execute(sender, IgnisCommands.IGNIS, new String[0]));
        assertTrue(sender.lastMessage.contains("still starting"));
    }

    @Test
    void delegatesToBoundHandler() {
        IgnisCommandBridge bridge = new IgnisCommandBridge();
        RecordingHandler handler = new RecordingHandler();
        bridge.bind(handler);

        RecordingSender sender = new RecordingSender();
        assertTrue(bridge.execute(sender, IgnisCommands.IGNIS, new String[] {"blocks"}));
        assertTrue(handler.executed);
    }

    @Test
    void rejectsMissingPermission() {
        IgnisCommandBridge bridge = new IgnisCommandBridge();
        bridge.bind(new RecordingHandler());

        RecordingSender sender = new RecordingSender();
        sender.permission = false;

        assertTrue(bridge.execute(sender, IgnisCommands.IGNIS, new String[0]));
        assertTrue(sender.lastMessage.contains("permission"));
    }

    private static final class RecordingHandler implements PluginCommandHandler {
        private boolean executed;

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            executed = true;
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return List.of();
        }
    }

    private static final class RecordingSender implements CommandSender {
        private boolean permission = true;
        private String lastMessage = "";

        @Override
        public void sendMessage(String message) {
            lastMessage = message;
        }

        @Override
        public void sendMessage(java.util.UUID uuid, String... messages) {
            if (messages.length > 0) {
                lastMessage = messages[0];
            }
        }

        @Override
        public String getName() {
            return "tester";
        }

        @Override
        public org.bukkit.Server getServer() {
            return null;
        }

        @Override
        public boolean isPermissionSet(String name) {
            return permission;
        }

        @Override
        public boolean isPermissionSet(org.bukkit.permissions.Permission perm) {
            return permission;
        }

        @Override
        public boolean hasPermission(String name) {
            return permission;
        }

        @Override
        public boolean hasPermission(org.bukkit.permissions.Permission perm) {
            return permission;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value, int ticks) {
            return null;
        }

        @Override
        public org.bukkit.permissions.PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, int ticks) {
            return null;
        }

        @Override
        public void removeAttachment(org.bukkit.permissions.PermissionAttachment attachment) {
        }

        @Override
        public void recalculatePermissions() {
        }

        @Override
        public java.util.Set<org.bukkit.permissions.PermissionAttachmentInfo> getEffectivePermissions() {
            return java.util.Set.of();
        }

        @Override
        public boolean isOp() {
            return permission;
        }

        @Override
        public void setOp(boolean value) {
            permission = value;
        }

        @Override
        public net.kyori.adventure.text.Component name() {
            return net.kyori.adventure.text.Component.text(getName());
        }

        @Override
        public org.bukkit.command.CommandSender.Spigot spigot() {
            return new org.bukkit.command.CommandSender.Spigot() {
            };
        }
    }
}
