package dev.rono.igniscore.api.model;

/**
 * Common surface for block and item extension definitions loaded from JARs.
 */
public interface ExtensionDefinition {

    String getId();

    String getExtensionId();
}
