package dev.rono.igniscore.bootstrap;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlatformBootloaderLoaderTest {

    @Test
    void selectsHighestPriorityBootloader() {
        Object host = new Object();

        PlatformBootloader selected = PlatformBootloaderLoader.resolve(host);

        assertEquals("test-high", selected.id());
    }

    @Test
    void bootsSelectedAdapter() {
        Object host = new Object();

        PlatformAdapter adapter = PlatformBootloaderLoader.boot(host);

        assertSame(TestBootloaderSupport.ADAPTER, adapter);
    }

    @Test
    void throwsWhenNoBootloaderMatches() {
        assertThrows(IllegalStateException.class, () -> PlatformBootloaderLoader.resolve(new UnmatchedHost()));
    }

    @Test
    void resolvesBootloadersWhenThreadContextClassLoaderDiffers() {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ClassLoader.getPlatformClassLoader());

            PlatformBootloader selected = PlatformBootloaderLoader.resolve(new Object());

            assertEquals("test-high", selected.id());
        } finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    static final class UnmatchedHost {
    }
}
