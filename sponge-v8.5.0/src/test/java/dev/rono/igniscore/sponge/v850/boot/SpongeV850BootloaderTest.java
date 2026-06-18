package dev.rono.igniscore.sponge.v850.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpongeV850BootloaderTest {

    @Test
    void identifiesSpongeApi850Slot() {
        SpongeV850Bootloader bootloader = new SpongeV850Bootloader();
        assertEquals("sponge-v8.5.0", bootloader.id());
        assertEquals(PlatformType.SPONGE, bootloader.platformType());
        assertEquals("1.20.x", bootloader.minecraftVersionRange());
        assertEquals(200, bootloader.priority());
    }
}
