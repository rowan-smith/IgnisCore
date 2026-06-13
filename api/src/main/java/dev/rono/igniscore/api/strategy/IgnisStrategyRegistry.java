package dev.rono.igniscore.api.strategy;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public interface IgnisStrategyRegistry {

    void register(IgnisStrategy strategy);

    void register(IgnisStrategyDescriptor descriptor, IgnisStrategy strategy);

    void unregister(String strategyId);

    void unregisterBySource(String sourcePluginId);

    Optional<IgnisStrategy> find(String strategyId);

    IgnisStrategy get(String strategyId);

    Collection<IgnisStrategyDescriptor> getDescriptors();

    boolean isRegistered(String strategyId);

    default <T extends IgnisStrategy> Optional<T> find(String strategyId, Class<T> type) {
        return find(strategyId)
                .filter(type::isInstance)
                .map(type::cast);
    }

    default IgnisBlockStrategy requireBlockStrategy(String extensionId, String definitionId) {
        return require(extensionId, IgnisBlockStrategy.class, () ->
                "Block type " + definitionId + " uses a non-block strategy from extension " + extensionId);
    }

    default IgnisItemStrategy requireItemStrategy(String extensionId, String definitionId) {
        return require(extensionId, IgnisItemStrategy.class, () ->
                "Item type " + definitionId + " uses a non-item strategy from extension " + extensionId);
    }

    default <T extends IgnisStrategy> T require(String extensionId, Class<T> type, Supplier<String> errorMessage) {
        IgnisStrategy strategy = get(extensionId);
        if (!type.isInstance(strategy)) {
            throw new IllegalStateException(errorMessage.get());
        }
        return type.cast(strategy);
    }
}
