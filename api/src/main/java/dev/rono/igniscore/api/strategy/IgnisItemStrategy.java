package dev.rono.igniscore.api.strategy;

/**
 * Runtime registration for custom item types.
 *
 * <p>Subscribe to item lifecycle events in the strategy constructor via
 * {@link IgnisStrategyContext#eventBus()}.</p>
 */
public interface IgnisItemStrategy extends IgnisStrategy {
}
