package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.PlacedClickSupport;
import dev.rono.igniscore.api.strategy.StrategyProfile;

/**
 * Routes placed-block clicks to core break/ignite/open handling via {@link BlockClickEvent#setResult}.
 */
public final class PlacedClickListener implements OnBlockClickListener {
    @FunctionalInterface
    public interface ProfileSource {
        StrategyProfile profile(BlockDefinition definition);
    }

    private final CustomBlockAction left;
    private final CustomBlockAction right;
    private final ProfileSource profileSource;

    private PlacedClickListener(CustomBlockAction left, CustomBlockAction right, ProfileSource profileSource) {
        this.left = left;
        this.right = right;
        this.profileSource = profileSource;
    }

    public static PlacedClickListener fixed(CustomBlockAction left, CustomBlockAction right) {
        return new PlacedClickListener(left, right, null);
    }

    public static PlacedClickListener forStrategy(IgnisBlockStrategy strategy) {
        return new PlacedClickListener(null, null, strategy::profile);
    }

    @Override
    public void onBlockClick(BlockClickEvent event) {
        CustomBlockAction action;
        if (profileSource != null) {
            StrategyProfile profile = profileSource.profile(event.block().definition());
            action = PlacedClickSupport.resolve(profile, event.interaction(), event.heldItem());
        } else {
            action = switch (event.interaction()) {
                case LEFT_CLICK_BLOCK -> left;
                case RIGHT_CLICK_BLOCK -> right;
                default -> CustomBlockAction.NONE;
            };
        }
        if (action != CustomBlockAction.NONE) {
            event.setResult(action);
        }
    }
}
