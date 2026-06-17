package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;

/**
 * Fired on each scheduler tick while an active block instance counts down its fuse.
 */
public final class BlockTickEvent {
    private final RuntimeBlockInstance instance;

    public BlockTickEvent(RuntimeBlockInstance instance) {
        this.instance = instance;
    }

    public RuntimeBlockInstance instance() {
        return instance;
    }
}
