package dev.rono.igniscore.support;

import dev.rono.igniscore.api.port.IgnisLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StubBlockManagerTest {

    @Test
    void tracksRegisteredPlacedBlocksByBlockCoordinates() {
        StubBlockManager manager = StubBlockManager.with(TestDefinitions.block("nuke"), TestDefinitions.block("signal-charge"));
        IgnisLocation precise = new IgnisLocation("world", 1.9, 64.2, 2.1);

        manager.registerPlacedBlock(precise, "nuke", null);

        assertEquals("nuke", manager.getPlacedBlockType(new IgnisLocation("world", 1, 64, 2)));
    }

    @Test
    void exposesConfiguredBlockTypes() {
        StubBlockManager manager = StubBlockManager.with(TestDefinitions.block("nuke"));

        assertEquals("nuke", manager.getBlockTypes().get("nuke").getId());
    }
}
