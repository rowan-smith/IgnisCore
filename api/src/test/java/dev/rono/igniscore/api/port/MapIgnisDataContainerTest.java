package dev.rono.igniscore.api.port;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapIgnisDataContainerTest {

    @Test
    void storesPrimitiveValues() {
        MapIgnisDataContainer container = new MapIgnisDataContainer();

        container.setBoolean("armed", true);
        container.setInt("fuse", 40);
        container.setDouble("power", 4.5);
        container.setString("owner", "player-1");

        assertTrue(container.getBoolean("armed"));
        assertEquals(40, container.getInt("fuse"));
        assertEquals(4.5, container.getDouble("power"));
        assertEquals("player-1", container.getString("owner"));
        assertTrue(container.hasKey("owner"));
    }

    @Test
    void returnsDefaultsForMissingOrMismatchedValues() {
        MapIgnisDataContainer container = new MapIgnisDataContainer();
        container.setString("label", "test");

        assertFalse(container.getBoolean("missing"));
        assertEquals(0, container.getInt("missing"));
        assertEquals(0.0, container.getDouble("missing"));
        assertNull(container.getString("missing"));
        assertFalse(container.hasKey("missing"));
    }
}
