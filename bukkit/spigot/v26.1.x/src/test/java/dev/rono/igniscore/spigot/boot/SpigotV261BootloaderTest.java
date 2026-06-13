package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpigotV261BootloaderTest {

    @Test
    void exposesSpigotMetadata() {
        SpigotV261Bootloader bootloader = new SpigotV261Bootloader();
        assertEquals("spigot-v26.1.x", bootloader.id());
        assertEquals(PlatformType.SPIGOT, bootloader.platformType());
        assertEquals("26.1.x", bootloader.minecraftVersionRange());
        assertEquals(50, bootloader.priority());
    }
}
