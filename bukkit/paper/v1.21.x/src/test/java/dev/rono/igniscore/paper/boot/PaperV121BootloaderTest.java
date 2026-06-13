package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaperV121BootloaderTest {

    @Test
    void exposesPaperMetadata() {
        PaperV121Bootloader bootloader = new PaperV121Bootloader();
        assertEquals("paper-v1.21.x", bootloader.id());
        assertEquals(PlatformType.PAPER, bootloader.platformType());
        assertEquals("1.21.x", bootloader.minecraftVersionRange());
        assertEquals(100, bootloader.priority());
    }
}
