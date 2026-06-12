package dev.rono.igniscore.sponge.v1200;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reserved SpongeAPI 12.x (Minecraft 1.21.x) adapter slot.
 * Full adapter will implement {@link dev.rono.igniscore.api.port.PlatformBootloader}.
 */
final class SpongeV1200StubTest {

    @Test
    void documentsTargetPlatform() {
        assertEquals(PlatformType.SPONGE, PlatformType.SPONGE);
    }
}
