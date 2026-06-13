package dev.rono.igniscore.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemVersionTest {
    @Test
    void parsesReleaseVersions() {
        SemVersion version = SemVersion.parse("1.2.3");
        assertEquals(1, version.major());
        assertEquals(2, version.minor());
        assertEquals(3, version.patch());
        assertFalse(version.isPreRelease());
    }

    @Test
    void parsesPreReleaseVersions() {
        SemVersion version = SemVersion.parse("1.0.0-rc1");
        assertTrue(version.isPreRelease());
        assertEquals("rc1", version.preRelease());
    }

    @Test
    void comparesReleaseOrdering() {
        assertTrue(SemVersion.parse("1.2.0").compareTo(SemVersion.parse("1.1.9")) > 0);
        assertTrue(SemVersion.parse("2.0.0").compareTo(SemVersion.parse("1.9.9")) > 0);
    }

    @Test
    void runtimeCompatibilityRequiresSameMajorAndNewEnoughRuntime() {
        SemVersion runtime = SemVersion.parse("1.2.0");
        assertTrue(SemVersion.isRuntimeCompatibleWith(runtime, SemVersion.parse("1.0.0")));
        assertTrue(SemVersion.isRuntimeCompatibleWith(runtime, SemVersion.parse("1.2.0")));
        assertFalse(SemVersion.isRuntimeCompatibleWith(runtime, SemVersion.parse("1.3.0")));
        assertFalse(SemVersion.isRuntimeCompatibleWith(runtime, SemVersion.parse("2.0.0")));
    }

    @Test
    void rejectsInvalidVersions() {
        assertThrows(IllegalArgumentException.class, () -> SemVersion.parse("1.0"));
    }
}
