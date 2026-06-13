package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpigotV120BootloaderTest {

    @Test
    void exposesSpigotMetadata() {
        SpigotV120Bootloader bootloader = new SpigotV120Bootloader();
        assertEquals("spigot-v1.20.x", bootloader.id());
        assertEquals(PlatformType.SPIGOT, bootloader.platformType());
        assertEquals("1.20.x", bootloader.minecraftVersionRange());
        assertEquals(50, bootloader.priority());
    }
}
