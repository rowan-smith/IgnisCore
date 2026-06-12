package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.strategies.DefaultExplosionStrategy;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class IgnisStrategyRegistryImpl implements IgnisStrategyRegistry {
    private final Map<String, IgnisStrategy> strategies = new ConcurrentHashMap<>();
    private final Map<String, IgnisStrategyDescriptor> descriptors = new ConcurrentHashMap<>();
    private final DefaultExplosionStrategy fallbackStrategy;

    @Inject
    public IgnisStrategyRegistryImpl(DefaultExplosionStrategy fallbackStrategy) {
        this.fallbackStrategy = fallbackStrategy;
        register(fallbackStrategy);
    }

    @Override
    public void register(IgnisStrategy strategy) {
        register(strategy.descriptor(), strategy);
    }

    @Override
    public void register(IgnisStrategyDescriptor descriptor, IgnisStrategy strategy) {
        String id = descriptor.getId().toLowerCase();
        strategies.put(id, strategy);
        descriptors.put(id, descriptor);
    }

    @Override
    public void unregister(String strategyId) {
        if ("default".equalsIgnoreCase(strategyId)) {
            return;
        }
        strategies.remove(strategyId.toLowerCase());
        descriptors.remove(strategyId.toLowerCase());
    }

    @Override
    public void unregisterBySource(String sourcePluginId) {
        descriptors.entrySet().stream()
                .filter(entry -> sourcePluginId.equalsIgnoreCase(entry.getValue().getSourcePlugin()))
                .map(Map.Entry::getKey)
                .forEach(this::unregister);
    }

    @Override
    public Optional<IgnisStrategy> find(String strategyId) {
        if (strategyId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(strategies.get(strategyId.toLowerCase()));
    }

    @Override
    public IgnisStrategy get(String strategyId) {
        if (strategyId == null) {
            return fallbackStrategy;
        }
        return strategies.getOrDefault(strategyId.toLowerCase(), fallbackStrategy);
    }

    @Override
    public Collection<IgnisStrategyDescriptor> getDescriptors() {
        return descriptors.values();
    }

    @Override
    public boolean isRegistered(String strategyId) {
        return strategyId != null && strategies.containsKey(strategyId.toLowerCase());
    }
}
