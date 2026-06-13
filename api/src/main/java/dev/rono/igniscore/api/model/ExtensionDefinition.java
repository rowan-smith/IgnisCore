package dev.rono.igniscore.api.model;

/**
 * Common surface for block and item extension definitions loaded from JARs.
 */
public sealed interface ExtensionDefinition permits BlockDefinition, ItemDefinition {

    String getId();

    String getExtensionId();
}
