package dev.rono.igniscore.api.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IgnisStrategyDescriptorTest {
    @Test
    void normalizesIdToLowerCase() {
        IgnisStrategyDescriptor descriptor = IgnisStrategyDescriptor.of("NuClEaR", "Nuclear", "1.0.0", "Tester");

        assertEquals("nuclear", descriptor.getId());
    }

    @Test
    void appliesDefaultsForMissingMetadata() {
        IgnisStrategyDescriptor descriptor = IgnisStrategyDescriptor.of("quake", null, null, null);

        assertEquals("quake", descriptor.getId());
        assertEquals("quake", descriptor.getName());
        assertEquals("1.0.0", descriptor.getVersion());
        assertEquals("unknown", descriptor.getAuthor());
        assertEquals("builtin", descriptor.getSourcePlugin());
    }

    @Test
    void storesSourcePluginWhenProvided() {
        IgnisStrategyDescriptor descriptor = IgnisStrategyDescriptor.of(
                "grenade", "Grenade", "2.1.0", "IgnisCore", "grenade");

        assertEquals("grenade", descriptor.getSourcePlugin());
        assertEquals("Grenade", descriptor.getName());
        assertEquals("2.1.0", descriptor.getVersion());
    }

    @Test
    void rejectsNullId() {
        assertThrows(NullPointerException.class,
                () -> new IgnisStrategyDescriptor(null, "name", "1.0.0", "author", "source"));
    }
}
