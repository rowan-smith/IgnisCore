package dev.rono.igniscore.service;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.TestDefinitions;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("RuntimeBlockService requires Paper NMS-backed NBT-API compounds")
class RuntimeBlockServiceTest extends MockBukkitTestBase {
    private RuntimeBlockService service;
    private Location location;

    @BeforeEach
    void setUpService() {
        service = new RuntimeBlockService();
        location = new Location(world, 1, 64, 2);
    }

    @Test
    void tracksInstancesByUuidAndLocation() {
        RuntimeBlockInstance instance = service.createInstance(TestDefinitions.block("nuke"), location);

        assertSame(instance, service.getInstance(instance.getUuid()));
        assertSame(instance, service.getInstanceAt(location));
        assertEquals(1, service.getActiveInstances().size());
        assertEquals(80, instance.getTicksLeft());
    }

    @Test
    void removeInstanceClearsIndexes() {
        RuntimeBlockInstance instance = service.createInstance(TestDefinitions.block("nuke"), location);

        service.removeInstance(instance.getUuid());

        assertNull(service.getInstance(instance.getUuid()));
        assertNull(service.getInstanceAt(location));
        assertTrue(service.getActiveInstances().isEmpty());
    }

    @Test
    void instanceSupportsNbtFlagsAndTicking() {
        RuntimeBlockInstance instance = service.createInstance(TestDefinitions.block("nuke"), location);

        instance.setTicksLeft(10);
        instance.tick();
        assertEquals(9, instance.getTicksLeft());

        instance.setFlag("armed", true);
        assertTrue(instance.getFlag("armed"));
        instance.getData().setFloat("ignis:blast_power", 12.5f);
        assertEquals(12.5f, instance.getData().getFloat("ignis:blast_power"));
    }
}
