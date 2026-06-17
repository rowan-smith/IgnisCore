package dev.rono.igniscore.api.port;

/**
 * Creates platform-native custom block and item stacks exposed as {@link IgnisItem}.
 *
 * <p>Used when the runtime needs item representations of registered extension
 * types rather than vanilla material keys.</p>
 */
public interface IgnisCustomItemFactory {

    /**
     * Creates an item stack for a custom block type.
     *
     * @param typeId extension block type id from configuration
     * @return item handle for the custom block
     */
    IgnisItem createBlockItem(String typeId);

    /**
     * Creates an item stack for a custom item type.
     *
     * @param typeId extension item type id from configuration
     * @return item handle for the custom item
     */
    IgnisItem createItem(String typeId);
}
