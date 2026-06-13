package dev.rono.igniscore.folia.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoliaV121BootloaderTest {

    @Test
    void hasHighestBukkitFamilyPriority() {
        FoliaV121Bootloader bootloader = new FoliaV121Bootloader();
        assertEquals("folia-v1.21.x", bootloader.id());
        assertEquals(PlatformType.FOLIA, bootloader.platformType());
        assertEquals("1.21.x", bootloader.minecraftVersionRange());
        assertEquals(150, bootloader.priority());
    }
}
