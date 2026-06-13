package dev.rono.igniscore.bootstrap;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpongeBootloaderSupportTest {

    @Test
    void acceptsHostWhenTypeAndVersionMatch() {
        TestPlugin plugin = new TestPlugin("1.20.6");

        assertTrue(SpongeBootloaderSupport.acceptsHost(plugin, TestPlugin.class, TestPlugin::version, 1, 20));
        assertFalse(SpongeBootloaderSupport.acceptsHost(plugin, TestPlugin.class, TestPlugin::version, 1, 21));
        assertFalse(SpongeBootloaderSupport.acceptsHost("other", TestPlugin.class, TestPlugin::version, 1, 20));
    }

    @Test
    void requireHostReturnsTypedInstance() {
        TestPlugin plugin = new TestPlugin("1.21.4");

        assertSame(plugin, SpongeBootloaderSupport.requireHost(plugin, TestPlugin.class, "test-bootloader"));
    }

    @Test
    void requireHostThrowsForWrongType() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> SpongeBootloaderSupport.requireHost("bad", TestPlugin.class, "test-bootloader"));

        assertEquals("Bootloader test-bootloader requires dev.rono.igniscore.bootstrap.SpongeBootloaderSupportTest$TestPlugin host",
                error.getMessage());
    }

    private record TestPlugin(String version) {
    }
}
