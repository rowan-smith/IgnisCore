package dev.rono.igniscore.api.extension;

/**
 * Entry point for self-contained block extension JARs in {@code plugins/IgnisCore/blocks/}.
 */
public interface IgnisBlockPlugin {

    void onLoad(BlockExtensionContext context);

    default void onUnload(BlockExtensionContext context) {}
}
