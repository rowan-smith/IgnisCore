package dev.rono.igniscore.bootstrap;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    static final class UnmatchedHost {
    }
}
