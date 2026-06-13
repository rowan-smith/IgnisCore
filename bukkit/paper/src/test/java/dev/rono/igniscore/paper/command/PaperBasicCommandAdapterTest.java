package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.PluginCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaperBasicCommandAdapterTest {

    @Test
    void exposesConfiguredPermissionAndLabel() {
        PaperBasicCommandAdapter adapter = new PaperBasicCommandAdapter(
                new NoOpHandler(), "ic", "igniscore.admin");
        assertEquals("igniscore.admin", adapter.permission());
    }

    private static final class NoOpHandler implements PluginCommandHandler {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                                 @NotNull String label, @NotNull String[] args) {
            return true;
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                          @NotNull String alias, @NotNull String[] args) {
            return List.of();
        }
    }
}
