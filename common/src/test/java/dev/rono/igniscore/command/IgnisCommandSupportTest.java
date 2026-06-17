package dev.rono.igniscore.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class IgnisCommandSupportTest {
    @Test
    void splitsArgumentsOnWhitespace() {
        assertArrayEquals(new String[] {"give", "Rono", "block", "spider-storm"},
                IgnisCommandSupport.splitArgs("give Rono block spider-storm"));
        assertArrayEquals(new String[0], IgnisCommandSupport.splitArgs("   "));
    }

    @Test
    void exposesIgnisCommandMetadata() {
        assertEquals("ignis", IgnisCommands.IGNIS);
        assertEquals("igniscore.admin", IgnisCommands.PERMISSION);
        assertEquals(1, IgnisCommands.ALIASES.size());
        assertEquals("ic", IgnisCommands.ALIASES.getFirst());
    }
}
