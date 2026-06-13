package dev.rono.igniscore.api.strategy;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisStrategyRegistryTest {
    @Test
    void requireFailsFastWhenExtensionStrategyMissing() {
        IgnisStrategyRegistry registry = new TestRegistry();

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> registry.requireBlockStrategy("missing-extension", "missing-block"));

        assertTrue(error.getMessage().contains("missing-extension"));
        assertTrue(error.getMessage().contains("no strategy registered"));
    }

    @Test
    void requireReturnsRegisteredBlockStrategy() {
        IgnisBlockStrategy strategy = new IgnisBlockStrategy() {
            @Override
            public IgnisStrategyDescriptor descriptor() {
                return IgnisStrategyDescriptor.of("nuke", "Nuke", "1.0.0", "test");
            }
        };
        TestRegistry registry = new TestRegistry();
        registry.register(strategy.descriptor(), strategy);

        assertEquals(strategy, registry.requireBlockStrategy("nuke", "nuke"));
    }

    @Test
    void requireRejectsRegisteredStrategyOfWrongKind() {
        TestRegistry registry = new TestRegistry();
        registry.register(IgnisStrategyDescriptor.of("item-only", "Item", "1.0.0", "test"),
                new IgnisItemStrategy() {
                    @Override
                    public IgnisStrategyDescriptor descriptor() {
                        return IgnisStrategyDescriptor.of("item-only", "Item", "1.0.0", "test");
                    }
                });

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> registry.requireBlockStrategy("item-only", "test"));
        assertTrue(error.getMessage().contains("non-block strategy"));
        assertFalse(error.getMessage().contains("no strategy registered"));
    }

    private static final class TestRegistry implements IgnisStrategyRegistry {
        private final Map<String, IgnisStrategy> strategies = new HashMap<>();

        @Override
        public void register(IgnisStrategy strategy) {
            register(strategy.descriptor(), strategy);
        }

        @Override
        public void register(IgnisStrategyDescriptor descriptor, IgnisStrategy strategy) {
            strategies.put(descriptor.getId().toLowerCase(), strategy);
        }

        @Override
        public void unregister(String strategyId) {
            strategies.remove(strategyId.toLowerCase());
        }

        @Override
        public void unregisterBySource(String sourcePluginId) {
        }

        @Override
        public Optional<IgnisStrategy> find(String strategyId) {
            return Optional.ofNullable(strategies.get(strategyId.toLowerCase()));
        }

        @Override
        public IgnisStrategy get(String strategyId) {
            return find(strategyId).orElseThrow();
        }

        @Override
        public Collection<IgnisStrategyDescriptor> getDescriptors() {
            return strategies.values().stream().map(IgnisStrategy::descriptor).toList();
        }

        @Override
        public boolean isRegistered(String strategyId) {
            return strategies.containsKey(strategyId.toLowerCase());
        }
    }
}
