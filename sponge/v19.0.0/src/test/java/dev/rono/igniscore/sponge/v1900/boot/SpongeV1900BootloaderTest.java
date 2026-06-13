package dev.rono.igniscore.sponge.v1900.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SpongeV1900BootloaderTest {

    @Test
    void identifiesSpongeApi190Slot() {
        SpongeV1900Bootloader bootloader = new SpongeV1900Bootloader();
        assertEquals("sponge-v19.0.0", bootloader.id());
        assertEquals(PlatformType.SPONGE, bootloader.platformType());
        assertEquals("26.1.x", bootloader.minecraftVersionRange());
        assertEquals(200, bootloader.priority());
    }

    @Test
    void rejectsNonSpongeHosts() {
        assertFalse(new SpongeV1900Bootloader().canBoot(new Object()));
    }
}
