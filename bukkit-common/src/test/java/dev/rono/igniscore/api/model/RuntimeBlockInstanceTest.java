package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.TestDefinitions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeBlockInstanceTest extends MockBukkitTestBase {
    @Test
    void initializesFromDefinitionAndTracksRuntimeState() {
        UUID uuid = UUID.randomUUID();
        IgnisLocation location = new IgnisLocation(world.getName(), 10, 64, -3);
        BlockDefinition definition = TestDefinitions.block("nuke");

        RuntimeBlockInstance instance = new RuntimeBlockInstance(uuid, definition, location);

        assertEquals(uuid, instance.getUuid());
        assertEquals("nuke", instance.getBlockDefinitionId());
        assertSame(definition, instance.getDefinition());
        assertSame(location, instance.getLocation());
        assertEquals(world.getName(), instance.getWorldName());
        assertEquals(0, instance.getTicksLeft());
        assertTrue(instance.getDisplayEntityIds().isEmpty());

        instance.setTicksLeft(40);
        instance.tick();
        assertEquals(39, instance.getTicksLeft());

        instance.setFlag("armed", true);
        assertTrue(instance.getFlag("armed"));
        assertFalse(instance.getFlag("missing"));
    }
}
