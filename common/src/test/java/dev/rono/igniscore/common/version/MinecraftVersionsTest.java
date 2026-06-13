package dev.rono.igniscore.common.version;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinecraftVersionsTest {

    @Test
    void parsesBukkitVersionString() {
        MinecraftVersions.ParsedVersion version = MinecraftVersions.parse("1.21.4-R0.1-SNAPSHOT");
        assertTrue(version.isAtLeast(1, 21));
        assertTrue(MinecraftVersions.matchesMinorLine("1.21.4-R0.1-SNAPSHOT", 1, 21));
        assertFalse(MinecraftVersions.matchesMinorLine("1.20.6-R0.1-SNAPSHOT", 1, 21));
    }

    @Test
    void parsesYearBasedVersionString() {
        MinecraftVersions.ParsedVersion version = MinecraftVersions.parse("26.1.2-R0.1-SNAPSHOT");
        assertTrue(version.isAtLeast(26, 1));
        assertTrue(MinecraftVersions.matchesMinorLine("26.1.2-R0.1-SNAPSHOT", 26, 1));
        assertFalse(MinecraftVersions.matchesMinorLine("1.21.4-R0.1-SNAPSHOT", 26, 1));
    }
}
