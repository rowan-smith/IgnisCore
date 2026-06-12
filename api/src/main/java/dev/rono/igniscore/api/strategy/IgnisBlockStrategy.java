package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Runtime behavior for custom block types.
 */
public interface IgnisBlockStrategy extends IgnisStrategy {

    default StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    default void onStaticPlace(BlockDefinition definition, IgnisLocation location) {
        onStaticPlace(definition, location, null);
    }

    default void onStaticPlace(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {}

    default void onStaticInteract(BlockDefinition definition, IgnisLocation location, IgnisPlayer player, CustomBlockAction action) {}

    default void onStaticBreak(BlockDefinition definition, IgnisLocation location) {
        onStaticBreak(definition, location, null);
    }

    default void onStaticBreak(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem) {}

    default void onPlace(RuntimeBlockInstance instance) {}

    default void onTick(RuntimeBlockInstance instance) {}

    default void onInteract(RuntimeBlockInstance instance, IgnisPlayer player) {}

    default void onBreak(RuntimeBlockInstance instance) {}

    default void onTrigger(RuntimeBlockInstance instance, Object context) {}
}
