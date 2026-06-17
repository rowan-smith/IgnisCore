package dev.rono.igniscore.support;

import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;

import java.util.Set;

public final class TestIgnisCore {
    private TestIgnisCore() {
    }

    public static IgnisStrategyRegistryImpl newStrategyRegistry() {
        return new IgnisStrategyRegistryImpl(new DefaultExplosionStrategy(NoopExtensionSupport.INSTANCE));
    }

    public static IgnisStrategyContext noopStrategyContext() {
        return new IgnisStrategyContext(
                null, null, null, null, null, null, null, permissiveIntegrationRegistry(),
                NoopExtensionSupport.INSTANCE);
    }

    public static IgnisIntegrationRegistry permissiveIntegrationRegistry() {
        return new IgnisIntegrationRegistry() {
            @Override
            public boolean isEnabled(String integrationId) {
                return true;
            }

            @Override
            public String providerName(String integrationId) {
                return "test";
            }

            @Override
            public Set<String> enabledIntegrationIds() {
                return Set.of();
            }

            @Override
            public void requireEnabled(String integrationId) {
            }
        };
    }
}
