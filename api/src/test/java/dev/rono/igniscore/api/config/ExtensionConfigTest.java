package dev.rono.igniscore.api.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionConfigTest {
    @Test
    void readsTypedValuesAndSections() {
        ExtensionConfig config = ExtensionConfig.of(Map.of(
                "power", 4.5,
                "armed", true,
                "label", "nuke",
                "nested", Map.of("fuse", 80, "enabled", false)
        ));

        assertEquals(4.5, config.getDouble("power", 1.0));
        assertEquals(4, config.getInt("power", 1));
        assertTrue(config.getBoolean("armed", false));
        assertEquals("nuke", config.getString("label", "missing"));
        assertEquals(1.0, config.getDouble("missing", 1.0));
        assertFalse(config.getBoolean("missing", false));
        assertEquals(80, config.section("nested").getInt("fuse", 0));
        assertFalse(config.section("nested").getBoolean("enabled", true));
        assertTrue(config.contains("power"));
    }

    @Test
    void emptyConfigIsShared() {
        assertSame(ExtensionConfig.empty(), ExtensionConfig.of(null));
        assertSame(ExtensionConfig.empty(), ExtensionConfig.of(Map.of()));
    }
}
