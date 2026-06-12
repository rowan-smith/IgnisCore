package dev.rono.igniscore.api.port;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;

/**
 * Platform adapter for spawning and updating custom block display entities.
 */
public interface BlockVisualRenderer {

    void spawnAnimatedDisplay(RuntimeBlockInstance instance);

    Object spawnStaticDisplay(IgnisLocation location, BlockDefinition definition);

    void updateAnimation(RuntimeBlockInstance instance);

    void removeDisplay(RuntimeBlockInstance instance);

    void removeStaticDisplay(Object nativeDisplay);
}
