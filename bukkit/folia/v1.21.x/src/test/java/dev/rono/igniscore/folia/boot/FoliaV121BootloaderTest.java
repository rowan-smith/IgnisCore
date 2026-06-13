package dev.rono.igniscore.folia.boot;

import dev.rono.igniscore.api.port.PlatformBootloader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoliaV121BootloaderTest {

    @Test
    void hasHighestBukkitFamilyPriority() {
        FoliaV121Bootloader bootloader = new FoliaV121Bootloader();
        assertEquals("folia-v1.21.x", bootloader.id());
        assertEquals(150, bootloader.priority());
    }
}
