package dev.rono.igniscore.support;

import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;

public final class TestIgnisCore {
    private TestIgnisCore() {
    }

    public static IgnisStrategyRegistryImpl newStrategyRegistry() {
        return new IgnisStrategyRegistryImpl(new DefaultExplosionStrategy(NoopExtensionSupport.INSTANCE));
    }
}
