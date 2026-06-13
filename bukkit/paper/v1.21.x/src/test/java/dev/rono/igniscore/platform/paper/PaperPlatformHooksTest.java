package dev.rono.igniscore.platform.paper;

import dev.rono.igniscore.platform.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaperPlatformHooksTest {

    @Test
    void reportsPaperPlatformType() {
        assertEquals(PlatformType.PAPER, new PaperPlatformHooks().getPlatformType());
    }
}
