package dev.rono.igniscore.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisApiVersionTest {
    @Test
    void currentVersionIsStable() {
        assertEquals("1.0.0", IgnisApiVersion.CURRENT);
        assertEquals(SemVersion.parse("1.0.0"), IgnisApiVersion.CURRENT_SEMVER);
    }

    @Test
    void acceptsMatchingApiVersion() {
        assertDoesNotThrow(() -> IgnisApiVersion.requireCompatible("1.0.0", "test-extension"));
    }

    @Test
    void acceptsOlderMinorOnSameMajorLine() {
        assertDoesNotThrow(() -> IgnisApiVersion.requireCompatible("1.0.0", "legacy-extension"));
    }

    @Test
    void rejectsNewerRequiredApiVersion() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> IgnisApiVersion.requireCompatible("1.2.0", "future-extension"));

        assertTrue(error.getMessage().contains("future-extension"));
        assertTrue(error.getMessage().contains("1.2.0"));
    }

    @Test
    void rejectsDifferentMajorApiVersion() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> IgnisApiVersion.requireCompatible("2.0.0", "next-major-extension"));

        assertTrue(error.getMessage().contains("2.0.0"));
    }
}
