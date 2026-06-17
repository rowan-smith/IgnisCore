package dev.rono.igniscore.api;

import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;

import java.util.Collection;
import java.util.Map;

/**
 * Public runtime facade implemented by the core runtime and bound to {@link IgnisCoreAPI}.
 *
 * <p>Platform modules provide a single implementation at startup. Methods mirror the static
 * accessors on {@link IgnisCoreAPI} and expose block/item lifecycle, strategy registration,
 * and shared services.</p>
 */
public interface IgnisCoreFacade {
    /**
     * Returns all registered custom block definitions keyed by type id.
     *
     * @return block type id to definition map
     */
    Map<String, BlockDefinition> getBlockTypes();

    /**
     * Returns all registered custom item definitions keyed by type id.
     *
     * @return item type id to definition map
     */
    Map<String, ItemDefinition> getItemTypes();

    /**
     * Immediately triggers an active block instance at the given location.
     *
     * @param location world position of the block
     * @param typeId custom block type id
     * @param context optional trigger context passed to the strategy
     * @return the triggered runtime instance
     */
    RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context);

    /**
     * Starts the ignition lifecycle for a placed custom block at the given location.
     *
     * @param location world position of the placed block
     * @param context optional ignition context passed to the strategy
     * @return the newly created active runtime instance, or {@code null} if ignition did not occur
     */
    RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context);

    /**
     * Returns the custom block type id at a location, if any.
     *
     * @param location world position to inspect
     * @return registered type id, or {@code null} when no custom block is placed there
     */
    String getPlacedBlockType(IgnisLocation location);

    /**
     * Returns all block instances currently in the active (fused) lifecycle.
     *
     * @return collection of ticking or pending runtime instances
     */
    Collection<RuntimeBlockInstance> getActiveBlocks();

    /**
     * Creates a platform item stack representing a placeable custom block.
     *
     * @param typeId custom block type id
     * @return platform-neutral item handle
     */
    IgnisItem createBlockItem(String typeId);

    /**
     * Creates a platform item stack for a custom item type.
     *
     * @param typeId custom item type id
     * @return platform-neutral item handle
     */
    IgnisItem createItem(String typeId);

    /**
     * Returns the registry of loaded block and item behavior strategies.
     *
     * @return strategy registry backed by the runtime
     */
    IgnisStrategyRegistry getStrategyRegistry();

    /**
     * Returns the NBT read/write service for items and entities.
     *
     * @return platform-neutral NBT service
     */
    IgnisNbtService getNbtService();

    /**
     * Returns the optional client protocol integration service.
     *
     * @return protocol service; may report disabled when no integration is present
     */
    IgnisProtocolService getProtocolService();

    /**
     * Returns the visual and audio effect service.
     *
     * @return effect service for particles, sounds, and fake explosions
     */
    IgnisEffectService getEffectService();

    /**
     * Returns the region editing service (WorldEdit when available, ignis-world fallback).
     */
    IgnisRegionService getRegionService();

    /**
     * Returns the hologram integration service.
     */
    IgnisHologramService getHologramService();

    /**
     * Returns the NPC integration service.
     */
    IgnisNpcService getNpcService();

    /**
     * Returns the integration availability registry for extension manifests.
     */
    IgnisIntegrationRegistry getIntegrationRegistry();

    /**
     * Returns the platform-neutral event bus for lifecycle hooks and observers.
     *
     * @return shared event bus instance
     */
    IgnisEventBus eventBus();

    /**
     * Reloads extension JARs and refreshes block/item definitions and strategies.
     */
    void reloadExtensions();
}
