package dev.rono.igniscore.api.model;

/**
 * Common surface for block and item extension definitions loaded from JAR {@code config.yml}.
 *
 * <p>The {@link #getId() config id} is the in-game type id used by commands and NBT.
 * {@link #getExtensionId()} matches the manifest {@code id} that registers the strategy.</p>
 *
 * @see BlockDefinition
 * @see ItemDefinition
 */
public sealed interface ExtensionDefinition permits BlockDefinition, ItemDefinition {

    /**
     * @return in-game type id from {@code config.yml}
     */
    String getId();

    /**
     * @return manifest strategy id used to register the extension's
     *         {@link dev.rono.igniscore.api.strategy.IgnisStrategy}
     */
    String getExtensionId();
}
