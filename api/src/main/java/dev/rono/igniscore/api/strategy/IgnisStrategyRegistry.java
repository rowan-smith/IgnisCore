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

    /**
     * Returns a registered strategy, or the built-in {@code default} explosion strategy when the id
     * is missing. Prefer {@link #requireBlockStrategy} / {@link #requireItemStrategy} for extension
     * lookups so unregistered extension ids fail fast.
     */
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
        if (extensionId == null || extensionId.isBlank()) {
            throw new IllegalStateException(errorMessage.get() + " (extension id is missing)");
        }
        Optional<IgnisStrategy> strategy = find(extensionId);
        if (strategy.isEmpty()) {
            throw new IllegalStateException(
                    errorMessage.get() + " (no strategy registered for extension id '" + extensionId + "')");
        }
        if (!type.isInstance(strategy.get())) {
            throw new IllegalStateException(errorMessage.get());
        }
        return type.cast(strategy.get());
    }
}
