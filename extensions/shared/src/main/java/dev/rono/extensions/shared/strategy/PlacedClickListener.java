package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.strategy.PlacedClickSupport;

/**
 * Routes placed-block clicks to core break/ignite/open handling via {@link BlockClickEvent#setResult}.
 */
public final class PlacedClickListener implements OnBlockClickListener {
    private final CustomBlockAction left;
    private final CustomBlockAction right;

    private PlacedClickListener(CustomBlockAction left, CustomBlockAction right) {
        this.left = left;
        this.right = right;
    }

    public static PlacedClickListener fixed(CustomBlockAction left, CustomBlockAction right) {
        return new PlacedClickListener(left, right);
    }

    /**
     * Standard combustible explosive routing: left-click breaks, right-click with ignition item ignites.
     */
    public static PlacedClickListener combustible() {
        return new PlacedClickListener(CustomBlockAction.BREAK, CustomBlockAction.NONE);
    }

    @Override
    public void onBlockClick(BlockClickEvent event) {
        CustomBlockAction action = PlacedClickSupport.resolve(
                event.block().definition(), left, right, event.interaction(), event.heldItem());
        if (action != CustomBlockAction.NONE) {
            event.setResult(action);
        }
    }
}
