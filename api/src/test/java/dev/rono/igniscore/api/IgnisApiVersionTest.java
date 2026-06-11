package dev.rono.igniscore.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IgnisApiVersionTest {
    @Test
    void currentVersionIsStable() {
        assertEquals("1.0.0", IgnisApiVersion.CURRENT);
    }

    @Test
    void acceptsMatchingApiVersion() {
        assertDoesNotThrow(() -> IgnisApiVersion.requireCompatible("1.0.0", "test-extension"));
    }

    @Test
    void rejectsIncompatibleApiVersion() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> IgnisApiVersion.requireCompatible("2.0.0", "legacy-extension"));

        assertEquals("Extension 'legacy-extension' requires Ignis API 2.0.0 but runtime provides 1.0.0",
                error.getMessage());
    }
}
