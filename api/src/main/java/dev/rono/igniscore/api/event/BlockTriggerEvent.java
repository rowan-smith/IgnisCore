package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;

/**
 * Fired when an active block instance reaches the end of its fuse or is externally triggered.
 */
public final class BlockTriggerEvent {
    private final RuntimeBlockInstance instance;
    private final Object triggerContext;

    public BlockTriggerEvent(RuntimeBlockInstance instance, Object triggerContext) {
        this.instance = instance;
        this.triggerContext = triggerContext;
    }

    public RuntimeBlockInstance instance() {
        return instance;
    }

    public Object triggerContext() {
        return triggerContext;
    }
}
