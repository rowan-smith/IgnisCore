package dev.rono.igniscore.core;

import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.CustomBlockAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisStrategyRegistryImplTest {
    @Test
    void registersAndUnregistersBySource() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        registry.register(IgnisStrategyDescriptor.of("sonic_boom", "Sonic Boom", "1.0.0", "test", "plugin-a"),
                testStrategy("sonic_boom"));
        registry.register(IgnisStrategyDescriptor.of("quake", "Quake", "1.0.0", "test", "plugin-b"),
                testStrategy("quake"));

        assertTrue(registry.isRegistered("sonic_boom"));
        assertTrue(registry.isRegistered("quake"));

        registry.unregisterBySource("plugin-a");

        assertFalse(registry.isRegistered("sonic_boom"));
        assertTrue(registry.isRegistered("quake"));
    }

    @Test
    void fallsBackToDefaultStrategy() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();

        assertEquals("default", registry.get("missing-strategy").descriptor().getId());
        assertEquals("default", registry.get("default").descriptor().getId());
        assertTrue(registry.find(null).isEmpty());
        assertTrue(registry.find("missing-strategy").isEmpty());
        assertTrue(registry.find("default").isPresent());
    }

    @Test
    void lookupIsCaseInsensitive() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        registry.register(IgnisStrategyDescriptor.of("SoNiC", "Sonic", "1.0.0", "test"),
                testStrategy("sonic"));

        assertTrue(registry.isRegistered("sonic"));
        assertEquals("sonic", registry.get("SONIC").descriptor().getId());
    }

    @Test
    void protectsDefaultStrategyFromUnregister() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();

        registry.unregister("default");

        assertTrue(registry.isRegistered("default"));
        assertEquals("default", registry.get("default").descriptor().getId());
    }

    @Test
    void exposesRegisteredDescriptors() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        registry.register(IgnisStrategyDescriptor.of("quake", "Quake", "1.0.0", "test"),
                testStrategy("quake"));

        assertEquals(2, registry.getDescriptors().size());
        assertTrue(registry.getDescriptors().stream().anyMatch(descriptor -> "quake".equals(descriptor.getId())));
    }

    private IgnisStrategy testStrategy(String id) {
        return new IgnisBlockStrategy() {
            @Override
            public IgnisStrategyDescriptor descriptor() {
                return IgnisStrategyDescriptor.of(id, id, "1.0.0", "test");
            }

            @Override
            public StrategyProfile profile(BlockDefinition definition) {
                return StrategyProfile.builder()
                        .combustible(true)
                        .rightClickAction(CustomBlockAction.IGNITE)
                        .build();
            }
        };
    }
}
