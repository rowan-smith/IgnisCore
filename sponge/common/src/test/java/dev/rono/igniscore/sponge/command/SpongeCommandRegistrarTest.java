package dev.rono.igniscore.sponge.command;

import dev.rono.igniscore.command.IgnisCommands;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.command.Command;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpongeCommandRegistrarTest {

    @Test
    void registersPrimaryCommandAndAliases() {
        List<String> labels = new ArrayList<>();
        SpongeIgnisCommand command = new SpongeIgnisCommand(null, null, null, null, null) {
            @Override
            public Command.Parameterized build() {
                return Command.builder().build();
            }
        };

        SpongeCommandRegistrar.register(
                (pluginContainer, built, label) -> labels.add(label),
                null,
                command);

        assertEquals(IgnisCommands.ALIASES.size() + 1, labels.size());
        assertTrue(labels.contains("ignis"));
        assertTrue(labels.contains("ic"));
    }
}
