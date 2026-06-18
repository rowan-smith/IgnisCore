package dev.rono.igniscore.sponge.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SpongeV1200BootloaderTest {

    @Test
    void identifiesSpongeApi120Slot() {
        SpongeV1200Bootloader bootloader = new SpongeV1200Bootloader();
        assertEquals("sponge-v12.0.0", bootloader.id());
        assertEquals(PlatformType.SPONGE, bootloader.platformType());
        assertEquals("1.21.x", bootloader.minecraftVersionRange());
        assertEquals(200, bootloader.priority());
    }

    @Test
    void rejectsNonSpongeHosts() {
        assertFalse(new SpongeV1200Bootloader().canBoot(new Object()));
    }
}
