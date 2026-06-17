package dev.rono.igniscore.service;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuntimeBlockServiceTest {
    private RuntimeBlockService service;
    private IgnisLocation location;

    @BeforeEach
    void setUp() {
        service = new RuntimeBlockService();
        location = new IgnisLocation("world", 1, 64, 2);
    }

    @Test
    void tracksInstancesByUuidAndLocation() {
        RuntimeBlockInstance instance = service.createInstance(sampleDefinition(), location);

        assertSame(instance, service.getInstance(instance.getUuid()));
        assertSame(instance, service.getInstanceAt(location));
        assertEquals(1, service.getActiveInstances().size());
        assertEquals(0, instance.getTicksLeft());
    }

    @Test
    void removeInstanceClearsIndexes() {
        RuntimeBlockInstance instance = service.createInstance(sampleDefinition(), location);

        service.removeInstance(instance.getUuid());

        assertNull(service.getInstance(instance.getUuid()));
        assertNull(service.getInstanceAt(location));
        assertTrue(service.getActiveInstances().isEmpty());
    }

    @Test
    void instanceSupportsDataFlagsAndTicking() {
        RuntimeBlockInstance instance = service.createInstance(sampleDefinition(), location);

        instance.setTicksLeft(10);
        instance.tick();
        assertEquals(9, instance.getTicksLeft());

        instance.setFlag("armed", true);
        assertTrue(instance.getFlag("armed"));
        instance.getData().setDouble("ignis:blast_power", 12.5);
        assertEquals(12.5, instance.getData().getDouble("ignis:blast_power"));
    }

    private static BlockDefinition sampleDefinition() {
        return new BlockDefinition(
                "nuke",
                "paper",
                "carrot_on_a_stick",
                Component.text("Nuke"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", 80, "radius", 4.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "nuke");
    }
}
