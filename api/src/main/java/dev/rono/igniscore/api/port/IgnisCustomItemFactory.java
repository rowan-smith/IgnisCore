package dev.rono.igniscore.api.port;

/**
 * Creates platform-native custom block and item stacks exposed as {@link IgnisItem}.
 */
public interface IgnisCustomItemFactory {

    IgnisItem createBlockItem(String typeId);

    IgnisItem createItem(String typeId);
}
