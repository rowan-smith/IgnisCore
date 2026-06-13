package dev.rono.igniscore.resourcepack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcePackStorageTest {
    @TempDir
    Path tempDir;

    @Test
    void determineRetainedHashesAlwaysIncludesActiveHash() throws Exception {
        writePack("aaa");
        writePack("bbb");

        Set<String> retained = ResourcePackStorage.determineRetainedHashes(tempDir, "bbb", 1);

        assertEquals(Set.of("bbb"), retained);
    }

    @Test
    void determineRetainedHashesKeepsMostRecentPacks() throws Exception {
        writePack("old");
        Thread.sleep(5);
        writePack("middle");
        Thread.sleep(5);
        writePack("new");

        Set<String> retained = ResourcePackStorage.determineRetainedHashes(tempDir, "missing", 2);

        assertEquals(2, retained.size());
        assertTrue(retained.contains("new"));
        assertFalse(retained.contains("old"));
    }

    @Test
    void deleteUnretainedPacksRemovesOnlyUnneededFiles() throws Exception {
        writePack("keep");
        writePack("drop");

        int deleted = ResourcePackStorage.deleteUnretainedPacks(tempDir, Set.of("keep"));

        assertEquals(1, deleted);
        assertTrue(Files.exists(tempDir.resolve("resourcepack_keep.zip")));
        assertFalse(Files.exists(tempDir.resolve("resourcepack_drop.zip")));
    }

    private Path writePack(String hash) throws Exception {
        Path path = tempDir.resolve("resourcepack_" + hash + ".zip");
        Files.writeString(path, hash);
        return path;
    }
}
