package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisInteraction;
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
 *   <li>{@link #onPlaced} — after the custom block is registered at a location</li>
 *   <li>{@link #onPlacedClick} — player click; return {@link CustomBlockAction} for core handling</li>
 *   <li>{@link #onPlacedInteract} — follow-up for {@link CustomBlockAction#OPEN} and similar custom actions</li>
 *   <li>{@link #onPlacedBreak} — block removed (creative break, mining complete, or ignite prep)</li>
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
 * materials. Click behavior is implemented in {@link #onPlacedClick}; YAML {@code interactions}
 * is optional tuning data (sounds, particles) via {@link BlockDefinition#getInteractionConfig()}.</p>
 */
public interface IgnisBlockStrategy extends IgnisStrategy {

    default StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    default void onPlaced(BlockDefinition definition, IgnisLocation location) {
        onPlaced(definition, location, null);
    }

    default void onPlaced(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {}

    /**
     * Decide how a placed block responds to a player click.
     *
     * @return {@link CustomBlockAction#NONE} to ignore; {@link CustomBlockAction#HANDLED} when this
     *         method performed custom logic; {@link CustomBlockAction#BREAK} / {@link CustomBlockAction#IGNITE}
     *         for core services; {@link CustomBlockAction#OPEN} to delegate to {@link #onPlacedInteract}
     */
    default CustomBlockAction onPlacedClick(BlockDefinition definition,
                                            IgnisLocation location,
                                            IgnisPlayer player,
                                            IgnisInteraction interaction,
                                            IgnisItem heldItem) {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(definition.getBehaviorConfig());
        StrategyProfile profile = behavior.merge(profile(definition));
        if (!behavior.isEmpty()) {
            return behavior.resolve(interaction, profile, materialKey(heldItem));
        }
        return PlacedClickSupport.resolve(profile, interaction, heldItem);
    }

    private static String materialKey(IgnisItem heldItem) {
        if (heldItem == null || heldItem.isAir()) {
            return "AIR";
        }
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }

    default void onPlacedInteract(BlockDefinition definition,
                                  IgnisLocation location,
                                  IgnisPlayer player,
                                  IgnisInteraction interaction,
                                  IgnisItem heldItem,
                                  CustomBlockAction action) {
        onPlacedInteract(definition, location, player, action);
    }

    default void onPlacedInteract(BlockDefinition definition, IgnisLocation location, IgnisPlayer player, CustomBlockAction action) {}

    default void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        onPlacedBreak(definition, location, null);
    }

    default void onPlacedBreak(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem) {}

    default void onPlace(RuntimeBlockInstance instance) {}

    default void onTick(RuntimeBlockInstance instance) {}

    default void onInteract(RuntimeBlockInstance instance, IgnisPlayer player) {}

    default void onBreak(RuntimeBlockInstance instance) {}

    default void onTrigger(RuntimeBlockInstance instance, Object context) {}
}
