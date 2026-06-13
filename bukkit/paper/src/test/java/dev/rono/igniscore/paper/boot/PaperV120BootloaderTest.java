package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaperV120BootloaderTest {

    @Test
    void exposesPaperMetadata() {
        PaperV120Bootloader bootloader = new PaperV120Bootloader();
        assertEquals("paper-v1.20.x", bootloader.id());
        assertEquals(PlatformType.PAPER, bootloader.platformType());
        assertEquals("1.20.x", bootloader.minecraftVersionRange());
        assertEquals(100, bootloader.priority());
    }
}
