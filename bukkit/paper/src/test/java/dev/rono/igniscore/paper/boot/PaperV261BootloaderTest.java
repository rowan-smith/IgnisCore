package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaperV261BootloaderTest {

    @Test
    void exposesPaperMetadata() {
        PaperV261Bootloader bootloader = new PaperV261Bootloader();
        assertEquals("paper-v26.1.x", bootloader.id());
        assertEquals(PlatformType.PAPER, bootloader.platformType());
        assertEquals("26.1.x", bootloader.minecraftVersionRange());
        assertEquals(100, bootloader.priority());
    }
}
