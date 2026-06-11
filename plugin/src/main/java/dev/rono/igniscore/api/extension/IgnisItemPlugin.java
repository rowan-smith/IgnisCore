package dev.rono.igniscore.api.extension;

/**
 * Entry point for self-contained item extension JARs in {@code plugins/IgnisCore/items/}.
 */
public interface IgnisItemPlugin {

    void onLoad(ItemExtensionContext context);

    default void onUnload(ItemExtensionContext context) {}
}
