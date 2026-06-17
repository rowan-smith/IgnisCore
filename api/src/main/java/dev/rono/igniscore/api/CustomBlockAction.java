package dev.rono.igniscore.api;

/**
 * Outcome of a placed-block click handled by {@link dev.rono.igniscore.api.strategy.IgnisBlockStrategy#onPlacedClick}.
 *
 * <p>The core runtime inspects this value to decide whether to run built-in break/ignite services,
 * delegate to custom interaction logic, or leave the click unhandled.</p>
 */
public enum CustomBlockAction {
    /** Platform should not intercept this click. */
    NONE,
    /** Core break service handles mining/removal. */
    BREAK,
    /** Core ignition service starts the active fuse lifecycle. */
    IGNITE,
    /** Custom open behavior; core delegates to {@link dev.rono.igniscore.api.strategy.IgnisBlockStrategy#onPlacedInteract}. */
    OPEN,
    /** Strategy already handled the click in {@link dev.rono.igniscore.api.strategy.IgnisBlockStrategy#onPlacedClick}. */
    HANDLED
}
