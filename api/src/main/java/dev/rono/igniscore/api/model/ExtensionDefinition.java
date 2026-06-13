package dev.rono.igniscore.api.model;

/**
 * Common surface for block and item extension definitions loaded from JARs.
 *
 * @see dev.rono.igniscore.api.model.BlockDefinition
 * @see dev.rono.igniscore.api.model.ItemDefinition
 */
public sealed interface ExtensionDefinition permits BlockDefinition, ItemDefinition {

    /** In-game type id from {@code config.yml}. */
    String getId();

    /** Manifest strategy id used to register the extension's {@link dev.rono.igniscore.api.strategy.IgnisStrategy}. */
    String getExtensionId();
}
