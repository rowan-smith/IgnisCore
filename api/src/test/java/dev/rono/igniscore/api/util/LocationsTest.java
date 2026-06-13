package dev.rono.igniscore.api.util;

import dev.rono.igniscore.api.port.IgnisLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationsTest {

    @Test
    void toBlockFloorsCoordinates() {
        IgnisLocation location = new IgnisLocation("world", 1.9, -2.3, 3.1);

        IgnisLocation block = Locations.toBlock(location);

        assertEquals(1.0, block.x());
        assertEquals(-3.0, block.y());
        assertEquals(3.0, block.z());
        assertEquals("world", block.worldName());
    }

    @Test
    void toCenterOffsetsToBlockCenter() {
        UUID worldId = UUID.randomUUID();
        IgnisLocation location = new IgnisLocation(worldId, "world", 1.1, 4.9, -1.2, 90f, 0f);

        IgnisLocation center = Locations.toCenter(location);

        assertEquals(1.5, center.x());
        assertEquals(4.5, center.y());
        assertEquals(-1.5, center.z());
        assertEquals(worldId, center.worldId());
        assertEquals(90f, center.yaw());
    }
}
