package dev.rono.igniscore.api.strategy;

/**
 * Runtime registration for custom item types.
 *
 * <p>Implement {@link #registerEvents()} and subscribe to {@code onItemClick} via helpers on
 * {@link AbstractIgnisItemStrategy}.</p>
 */
public interface IgnisItemStrategy extends IgnisStrategy {
}
