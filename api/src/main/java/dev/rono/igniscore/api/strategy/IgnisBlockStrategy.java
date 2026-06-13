package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * Runtime behavior for custom block types.
 *
 * <h2>Two lifecycles</h2>
 * <p>Blocks have a <em>placed</em> lifecycle while the barrier block exists in the world, and an
 * <em>active</em> lifecycle after ignition when a {@link RuntimeBlockInstance} ticks until trigger.</p>
 *
 * <h3>Placed block callbacks</h3>
 * <ul>
 *   <li>{@link #onStaticPlace} — after the custom block is registered at a location</li>
 *   <li>{@link #onStaticInteract} — player click resolved to a {@link CustomBlockAction}</li>
 *   <li>{@link #onStaticBreak} — block removed (creative break, mining complete, or ignite prep)</li>
 * </ul>
 *
 * <h3>Active block callbacks</h3>
 * <ul>
 *   <li>{@link #onPlace} — active instance created (typically after ignite)</li>
 *   <li>{@link #onTick} — repeating scheduler tick while fuse counts down</li>
 *   <li>{@link #onTrigger} — fuse elapsed or external trigger; explosion/effect logic runs here</li>
 *   <li>{@link #onBreak} / {@link #onInteract} — optional active-instance hooks</li>
 * </ul>
 *
 * <p>Override {@link #profile} to declare combustibility, default click actions, and ignition
 * materials. YAML {@code interactions} and {@code breaking} sections on the {@link BlockDefinition}
 * override profile defaults at runtime.</p>
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
