package dev.rono.igniscore.paper.command;

import dev.rono.igniscore.command.IgnisCommands;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaperBasicCommandAdapterTest {

    @Test
    void exposesConfiguredPermission() {
        PaperBasicCommandAdapter adapter = new PaperBasicCommandAdapter(IgnisCommands.IGNIS, IgnisCommands.PERMISSION);
        assertEquals(IgnisCommands.PERMISSION, adapter.permission());
    }

    @Test
    void requiresBoundHandlerBeforeExecution() {
        PaperBasicCommandAdapter adapter = new PaperBasicCommandAdapter(IgnisCommands.IGNIS, IgnisCommands.PERMISSION);
        assertThrows(IllegalStateException.class, () -> adapter.execute(null, new String[0]));
    }
}
