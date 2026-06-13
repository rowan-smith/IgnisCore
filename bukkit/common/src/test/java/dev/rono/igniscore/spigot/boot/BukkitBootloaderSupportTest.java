package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BukkitBootloaderSupportTest {

    @Test
    void rejectsNonJavaPluginHosts() {
        assertFalse(BukkitBootloaderSupport.isJavaPluginHost(new Object()));
        assertFalse(BukkitBootloaderSupport.acceptsHost(new Object(), PlatformType.SPIGOT, 1, 21));
        assertFalse(BukkitBootloaderSupport.acceptsHost(new Object(), PlatformType.PAPER, 1, 21));
    }

    @Test
    void foliaRuntimeIsAbsentInUnitTests() {
        assertFalse(BukkitBootloaderSupport.isFoliaRuntime());
    }
}
