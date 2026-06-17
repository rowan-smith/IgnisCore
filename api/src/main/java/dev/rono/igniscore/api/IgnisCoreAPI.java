package dev.rono.igniscore.api;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;

import java.util.Collection;
import java.util.Map;

/**
 * Static entry point to the IgnisCore runtime for platform plugins and integrations.
 *
 * <p>The platform binds a concrete {@link IgnisCoreFacade} via {@link #init(IgnisCoreFacade)} during
 * startup. All methods delegate to that facade and throw {@link IllegalStateException} when called
 * before initialization.</p>
 *
 * <p>Extension strategies should use {@link dev.rono.igniscore.api.strategy.IgnisStrategyContext}
 * instead of this class to avoid crossing classloader boundaries.</p>
 *
 * @see IgnisCoreFacade
 */
public final class IgnisCoreAPI {
    private static IgnisCoreFacade facade;

    private IgnisCoreAPI() {
    }

    /**
     * Binds the runtime facade used by all static accessors.
     *
     * @param instance facade implementation provided by the core module
     */
    public static void init(IgnisCoreFacade instance) {
        facade = instance;
    }

    /**
     * Returns all registered custom block definitions keyed by type id.
     *
     * @return immutable view of block type id to definition
     */
    public static Map<String, BlockDefinition> getBlockTypes() {
        return requireFacade().getBlockTypes();
    }

    /**
     * Returns all registered custom item definitions keyed by type id.
     *
     * @return immutable view of item type id to definition
     */
    public static Map<String, ItemDefinition> getItemTypes() {
        return requireFacade().getItemTypes();
    }

    /**
     * Immediately triggers an active block instance at the given location.
     *
     * @param location world position of the block
     * @param typeId custom block type id
     * @param context optional trigger context passed to the strategy
     * @return the triggered runtime instance
     */
    public static RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
        return requireFacade().triggerBlock(location, typeId, context);
    }

    /**
     * Starts the ignition lifecycle for a placed custom block at the given location.
     *
     * @param location world position of the placed block
     * @param context optional ignition context passed to the strategy
     * @return the newly created active runtime instance, or {@code null} if ignition did not occur
     */
    public static RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context) {
        return requireFacade().ignitePlacedBlock(location, context);
    }

    /**
     * Returns the custom block type id at a location, if any.
     *
     * @param location world position to inspect
     * @return registered type id, or {@code null} when no custom block is placed there
     */
    public static String getPlacedBlockType(IgnisLocation location) {
        return requireFacade().getPlacedBlockType(location);
    }

    /**
     * Returns all block instances currently in the active (fused) lifecycle.
     *
     * @return collection of ticking or pending runtime instances
     */
    public static Collection<RuntimeBlockInstance> getActiveBlocks() {
        return requireFacade().getActiveBlocks();
    }

    /**
     * Creates a platform item stack representing a placeable custom block.
     *
     * @param typeId custom block type id
     * @return platform-neutral item handle
     */
    public static IgnisItem createBlockItem(String typeId) {
        return requireFacade().createBlockItem(typeId);
    }

    /**
     * Creates a platform item stack for a custom item type.
     *
     * @param typeId custom item type id
     * @return platform-neutral item handle
     */
    public static IgnisItem createItem(String typeId) {
        return requireFacade().createItem(typeId);
    }

    /**
     * Returns the registry of loaded block and item behavior strategies.
     *
     * @return strategy registry backed by the runtime
     */
    public static IgnisStrategyRegistry getStrategyRegistry() {
        return requireFacade().getStrategyRegistry();
    }

    /**
     * Returns the NBT read/write service for items and entities.
     *
     * @return platform-neutral NBT service
     */
    public static IgnisNbtService getNbtService() {
        return requireFacade().getNbtService();
    }

    /**
     * Returns the optional client protocol integration service.
     *
     * @return protocol service; may report disabled when no integration is present
     */
    public static IgnisProtocolService getProtocolService() {
        return requireFacade().getProtocolService();
    }

    /**
     * Returns the visual and audio effect service.
     *
     * @return effect service for particles, sounds, and fake explosions
     */
    public static IgnisEffectService getEffectService() {
        return requireFacade().getEffectService();
    }

    /**
     * Reloads extension JARs and refreshes block/item definitions and strategies.
     */
    public static void reloadExtensions() {
        requireFacade().reloadExtensions();
    }

    private static IgnisCoreFacade requireFacade() {
        if (facade == null) {
            throw new IllegalStateException("IgnisCoreAPI has not been initialized");
        }
        return facade;
    }
}
