package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpigotV121BootloaderTest {

    @Test
    void exposesSpigotMetadata() {
        SpigotV121Bootloader bootloader = new SpigotV121Bootloader();
        assertEquals("spigot-v1.21.x", bootloader.id());
        assertEquals(PlatformType.SPIGOT, bootloader.platformType());
        assertEquals("1.21.x", bootloader.minecraftVersionRange());
        assertEquals(50, bootloader.priority());
    }
}
