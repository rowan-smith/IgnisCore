package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.entity.Player;

/**
 * Defines runtime behavior for a custom block or item type.
 * Assign a strategy id in block config via {@code behavior.strategy}.
 */
public interface IgnisStrategy {

    IgnisStrategyDescriptor descriptor();

    /**
     * Default profile merged with block config. Block config wins on conflicts.
     */
    default StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    default void onPlace(RuntimeBlockInstance instance) {}

    default void onTick(RuntimeBlockInstance instance) {}

    default void onInteract(RuntimeBlockInstance instance, Player player) {}

    default void onBreak(RuntimeBlockInstance instance) {}

    default void onTrigger(RuntimeBlockInstance instance, Object context) {}
}
