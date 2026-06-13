package dev.rono.igniscore.api.port;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IgnisLocationTest {

    @Test
    void shortConstructorDefaultsRotation() {
        IgnisLocation location = new IgnisLocation("world", 1, 2, 3);

        assertEquals("world", location.worldName());
        assertEquals(0f, location.yaw());
        assertEquals(0f, location.pitch());
    }

    @Test
    void withYawPitchPreservesPosition() {
        IgnisLocation base = new IgnisLocation(UUID.randomUUID(), "world", 1, 2, 3, 0f, 0f);

        IgnisLocation rotated = base.withYawPitch(45f, 10f);

        assertEquals(base.x(), rotated.x());
        assertEquals(45f, rotated.yaw());
        assertEquals(10f, rotated.pitch());
    }

    @Test
    void addOffsetsCoordinates() {
        IgnisLocation location = new IgnisLocation("world", 1, 2, 3);

        IgnisLocation moved = location.add(0.5, -1, 2);

        assertEquals(1.5, moved.x());
        assertEquals(1, moved.y());
        assertEquals(5, moved.z());
    }

    @Test
    void rejectsNullWorldName() {
        assertThrows(NullPointerException.class,
                () -> new IgnisLocation((String) null, 0, 0, 0));
    }

    @Test
    void equalityUsesAllComponents() {
        UUID worldId = UUID.randomUUID();
        IgnisLocation first = new IgnisLocation(worldId, "world", 1, 2, 3, 4f, 5f);
        IgnisLocation second = new IgnisLocation(worldId, "world", 1, 2, 3, 4f, 5f);
        IgnisLocation different = new IgnisLocation(worldId, "world", 1, 2, 3, 4f, 6f);

        assertEquals(first, second);
        assertNotEquals(first, different);
    }
}
