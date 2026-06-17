package dev.rono.igniscore.api.strategy;

/**
 * Runtime registration for custom block types.
 *
 * <p>Subscribe to block lifecycle events in the strategy constructor via
 * {@link IgnisStrategyContext#eventBus()}. Declare combustibility, fuse timing, and click routing
 * in extension YAML and {@code OnBlockClickListener} subscriptions.</p>
 */
public interface IgnisBlockStrategy extends IgnisStrategy {
}
