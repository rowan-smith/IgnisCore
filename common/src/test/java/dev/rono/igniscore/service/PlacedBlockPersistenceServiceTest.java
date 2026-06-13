package dev.rono.igniscore.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacedBlockPersistenceServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void recordsAndRemovesPlacements() {
        try (PlacedBlockPersistenceService service = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(tempDir))) {
            IgnisLocation location = new IgnisLocation("world", 10, 64, -3);

            service.recordPlacement(location, "nuke");
            assertEquals("nuke", service.entriesInChunk("world", 0, -1).get("10,64,-3"));

            service.removePlacement(location);
            assertTrue(service.entriesInChunk("world", 0, -1).isEmpty());
        }
    }

    @Test
    void entriesInChunkFiltersByChunkBounds() {
        try (PlacedBlockPersistenceService service = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(tempDir))) {
            service.recordPlacement(new IgnisLocation("world", 0, 64, 0), "a");
            service.recordPlacement(new IgnisLocation("world", 15, 64, 15), "b");
            service.recordPlacement(new IgnisLocation("world", 16, 64, 0), "c");

            assertEquals(2, service.entriesInChunk("world", 0, 0).size());
            assertFalse(service.entriesInChunk("world", 0, 0).containsKey("16,64,0"));
            assertEquals("c", service.entriesInChunk("world", 1, 0).get("16,64,0"));
        }
    }

    @Test
    void chunkKeysForWorldReturnsIndexedChunks() {
        try (PlacedBlockPersistenceService service = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(tempDir))) {
            service.recordPlacement(new IgnisLocation("world", 0, 64, 0), "a");
            service.recordPlacement(new IgnisLocation("world", 16, 64, 0), "b");

            assertEquals(Set.of("0,0", "1,0"), service.chunkKeysForWorld("world"));
            assertTrue(service.chunkKeysForWorld("other").isEmpty());
        }
    }

    @Test
    void flushPersistsAsyncWritesToDisk() throws Exception {
        try (PlacedBlockPersistenceService service = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(tempDir))) {
            service.recordPlacement(new IgnisLocation("world", 1, 64, 2), "nuke");
            service.flush();

            String persisted = Files.readString(tempDir.resolve("placed-blocks.json"));
            assertTrue(persisted.contains("nuke"));
        }
    }

    @Test
    void migratesLegacyYamlIndexToJson() throws Exception {
        Path yaml = tempDir.resolve("placed-blocks.yml");
        Files.writeString(yaml, """
                world:
                  1,2,3: signal-charge
                """);

        try (PlacedBlockPersistenceService service = new PlacedBlockPersistenceService(
                CommonTestSupport.runtimeHost(tempDir))) {
            assertEquals("signal-charge", service.entriesInChunk("world", 0, 0).get("1,2,3"));
            assertTrue(Files.isRegularFile(tempDir.resolve("placed-blocks.json")));
            assertFalse(Files.isRegularFile(yaml));
            assertTrue(Files.isRegularFile(tempDir.resolve("placed-blocks.yml.migrated")));
        }
    }
}
