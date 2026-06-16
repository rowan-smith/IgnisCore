package dev.rono.igniscore.spigot.command;

import dev.rono.igniscore.command.IgnisCommands;
import dev.rono.igniscore.command.PluginCommandHandler;
import dev.rono.igniscore.support.MockBukkitTestBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BukkitCommandRegistrationTest extends MockBukkitTestBase {

    @Test
    void registersProgrammaticIgnisCommand() {
        RecordingHandler handler = new RecordingHandler();
        BukkitCommandRegistration.register(plugin, IgnisCommands.IGNIS, handler);

        PluginCommand command = plugin.getCommand(IgnisCommands.IGNIS);
        assertNotNull(command);
        assertEquals(IgnisCommands.DESCRIPTION, command.getDescription());
        assertEquals(IgnisCommands.ALIASES, command.getAliases());
        assertEquals(handler, command.getExecutor());
        assertEquals(handler, command.getTabCompleter());

        CommandSender sender = server.addPlayer("tester");
        sender.addAttachment(plugin, IgnisCommands.PERMISSION, true);
        assertTrue(command.execute(sender, IgnisCommands.IGNIS, new String[] {"blocks"}));
        assertTrue(handler.executed);
    }

    private static final class RecordingHandler implements PluginCommandHandler {
        private boolean executed;

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String[] args) {
            executed = true;
            return true;
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                          @NotNull String alias, @NotNull String[] args) {
            return List.of();
        }
    }
}
