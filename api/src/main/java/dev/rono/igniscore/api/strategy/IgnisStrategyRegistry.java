package dev.rono.igniscore.api.strategy;

import java.util.Collection;
import java.util.Optional;

public interface IgnisStrategyRegistry {

    void register(IgnisStrategy strategy);

    void register(IgnisStrategyDescriptor descriptor, IgnisStrategy strategy);

    void unregister(String strategyId);

    void unregisterBySource(String sourcePluginId);

    Optional<IgnisStrategy> find(String strategyId);

    IgnisStrategy get(String strategyId);

    Collection<IgnisStrategyDescriptor> getDescriptors();

    boolean isRegistered(String strategyId);
}
