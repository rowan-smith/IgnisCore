package dev.rono.igniscore.spigot.v120;

import dev.rono.igniscore.api.port.PlatformBootloader;
import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Stub module reserving the Spigot 1.20.x adapter slot.
 */
final class SpigotV120StubTest {

    @Test
    void noBootloaderRegisteredYet() {
        assertFalse(ServiceLoader.load(PlatformBootloader.class).stream()
                .map(ServiceLoader.Provider::get)
                .anyMatch(b -> b.id().contains("1.20")));
    }
}
