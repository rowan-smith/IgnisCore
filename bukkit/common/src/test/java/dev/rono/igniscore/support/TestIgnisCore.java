package dev.rono.igniscore.support;

import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;

public final class TestIgnisCore {
    private TestIgnisCore() {
    }

    public static IgnisStrategyRegistryImpl newStrategyRegistry() {
        IgnisEventBusImpl eventBus = new IgnisEventBusImpl();
        return new IgnisStrategyRegistryImpl(new DefaultExplosionStrategy(NoopExtensionSupport.INSTANCE, eventBus));
    }
}
