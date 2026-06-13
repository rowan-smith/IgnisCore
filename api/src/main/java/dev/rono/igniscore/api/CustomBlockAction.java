package dev.rono.igniscore.api;

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
