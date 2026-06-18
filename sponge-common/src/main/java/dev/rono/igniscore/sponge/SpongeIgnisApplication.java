package dev.rono.igniscore.sponge;

import com.google.inject.Inject;
import dev.rono.igniscore.core.IgnisRuntimeLifecycle;

public class SpongeIgnisApplication {
    private final IgnisRuntimeLifecycle lifecycle;

    @Inject
    public SpongeIgnisApplication(IgnisRuntimeLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void enable() {
        lifecycle.enable();
    }

    public void disable() {
        lifecycle.disable();
    }
}
