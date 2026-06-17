package dev.rono.igniscore.api.strategy;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Registry of loaded block and item behavior strategies.
 *
 * <p>Strategies are keyed by extension id. The core registers built-in and extension-supplied
 * implementations during startup and reload. Lookup helpers distinguish optional resolution from
 * fail-fast requirements used when resolving block/item definitions.</p>
 */
public interface IgnisStrategyRegistry {

    /**
     * Registers a strategy using metadata from {@link IgnisStrategy#descriptor()}.
     *
     * @param strategy strategy instance with a bound descriptor
     */
    void register(IgnisStrategy strategy);

    /**
     * Registers a strategy with an explicit descriptor.
     *
     * <p>When the strategy implements {@link AbstractIgnisStrategy}, the descriptor is also bound
     * on the instance.</p>
     *
     * @param descriptor registry metadata
     * @param strategy strategy implementation
     */
    void register(IgnisStrategyDescriptor descriptor, IgnisStrategy strategy);

    /**
     * Removes a strategy by id.
     *
     * @param strategyId normalized strategy id
     */
    void unregister(String strategyId);

    /**
     * Removes all strategies registered by the given source plugin.
     *
     * @param sourcePluginId plugin or extension id from {@link IgnisStrategyDescriptor#getSourcePlugin()}
     */
    void unregisterBySource(String sourcePluginId);

    /**
     * Looks up a strategy by id without substituting defaults.
     *
     * @param strategyId normalized strategy id
     * @return the registered strategy, or empty when not found
     */
    Optional<IgnisStrategy> find(String strategyId);

    /**
     * Returns a registered strategy, or the built-in {@code default} explosion strategy when the id
     * is missing.
     *
     * <p>Prefer {@link #requireBlockStrategy} / {@link #requireItemStrategy} for extension
     * lookups so unregistered extension ids fail fast.</p>
     *
     * @param strategyId normalized strategy id
     * @return registered strategy or built-in default fallback
     */
    IgnisStrategy get(String strategyId);

    /**
     * Returns metadata for all currently registered strategies.
     *
     * @return collection of strategy descriptors
     */
    Collection<IgnisStrategyDescriptor> getDescriptors();

    /**
     * Returns whether a strategy id is currently registered.
     *
     * @param strategyId normalized strategy id
     * @return {@code true} when {@link #find(String)} would return a value
     */
    boolean isRegistered(String strategyId);

    /**
     * Looks up a strategy and casts it to the requested type.
     *
     * @param strategyId normalized strategy id
     * @param type expected strategy subtype
     * @param <T> strategy type
     * @return matching strategy, or empty when missing or not an instance of {@code type}
     */
    default <T extends IgnisStrategy> Optional<T> find(String strategyId, Class<T> type) {
        return find(strategyId)
                .filter(type::isInstance)
                .map(type::cast);
    }

    /**
     * Requires a registered block strategy for an extension-backed block definition.
     *
     * @param extensionId extension id from the block definition
     * @param definitionId block type id used in error messages
     * @return block strategy instance
     * @throws IllegalStateException when the id is missing, unregistered, or not a block strategy
     */
    default IgnisBlockStrategy requireBlockStrategy(String extensionId, String definitionId) {
        return require(extensionId, IgnisBlockStrategy.class, () ->
                "Block type " + definitionId + " uses a non-block strategy from extension " + extensionId);
    }

    /**
     * Requires a registered item strategy for an extension-backed item definition.
     *
     * @param extensionId extension id from the item definition
     * @param definitionId item type id used in error messages
     * @return item strategy instance
     * @throws IllegalStateException when the id is missing, unregistered, or not an item strategy
     */
    default IgnisItemStrategy requireItemStrategy(String extensionId, String definitionId) {
        return require(extensionId, IgnisItemStrategy.class, () ->
                "Item type " + definitionId + " uses a non-item strategy from extension " + extensionId);
    }

    /**
     * Requires a registered strategy of the given type.
     *
     * @param extensionId extension id to resolve
     * @param type expected strategy subtype
     * @param errorMessage supplier of the base error message when lookup fails
     * @param <T> strategy type
     * @return strategy instance of {@code type}
     * @throws IllegalStateException when the id is blank, unregistered, or not an instance of {@code type}
     */
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
