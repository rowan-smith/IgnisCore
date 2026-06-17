package dev.rono.igniscore.api.port;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;

/**
 * Platform adapter for spawning and updating custom block display entities.
 *
 * <p>Custom blocks are often shown via display entities (item displays, block
 * displays) rather than real world blocks. This port manages their lifecycle
 * for animated and static presentations.</p>
 */
public interface BlockVisualRenderer {

    /**
     * Spawns display entity(ies) for an animated runtime block instance.
     *
     * @param instance placed block with animation state
     */
    void spawnAnimatedDisplay(RuntimeBlockInstance instance);

    /**
     * Spawns a static display at a location from a block definition.
     *
     * @param location world position for the display
     * @param definition block metadata driving model and appearance
     * @return opaque native display entity handle
     */
    Object spawnStaticDisplay(IgnisLocation location, BlockDefinition definition);

    /**
     * Updates display state for an animated instance (frame, rotation, etc.).
     *
     * @param instance runtime block whose display should refresh
     */
    void updateAnimation(RuntimeBlockInstance instance);

    /**
     * Removes display entities associated with a runtime block instance.
     *
     * @param instance placed block whose displays should be destroyed
     */
    void removeDisplay(RuntimeBlockInstance instance);

    /**
     * Removes a static display created by {@link #spawnStaticDisplay}.
     *
     * @param nativeDisplay opaque display entity from {@link #spawnStaticDisplay}
     */
    void removeStaticDisplay(Object nativeDisplay);
}
