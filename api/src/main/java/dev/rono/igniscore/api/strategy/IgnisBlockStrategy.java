package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;

/**
 * Runtime registration for custom block types.
 *
 * <p>Subscribe to block lifecycle events in the strategy constructor via
 * {@link IgnisStrategyContext#eventBus()}. Override {@link #profile} to declare combustibility,
 * fuse, and default click actions.</p>
 */
public interface IgnisBlockStrategy extends IgnisStrategy {

    default StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }
}
