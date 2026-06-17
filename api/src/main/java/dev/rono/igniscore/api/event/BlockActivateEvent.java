package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;

/**
 * Fired when an active (fused) block instance is created, typically after ignition.
 */
public final class BlockActivateEvent {
    private final RuntimeBlockInstance instance;

    public BlockActivateEvent(RuntimeBlockInstance instance) {
        this.instance = instance;
    }

    public RuntimeBlockInstance instance() {
        return instance;
    }
}
