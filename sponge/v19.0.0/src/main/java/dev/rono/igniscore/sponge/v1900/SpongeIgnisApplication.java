package dev.rono.igniscore.sponge.v1900;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.core.IgnisRuntimeLifecycle;
import dev.rono.igniscore.sponge.v1900.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.v1900.listener.SpongeItemListener;

import java.util.List;

public class SpongeIgnisApplication {
    private final PlatformAdapter platformAdapter;
    private final IgnisRuntimeLifecycle lifecycle;
    private final SpongeItemListener itemListener;
    private final SpongeBlockListener blockListener;

    @Inject
    public SpongeIgnisApplication(PlatformAdapter platformAdapter,
                                  IgnisRuntimeLifecycle lifecycle,
                                  SpongeItemListener itemListener,
                                  SpongeBlockListener blockListener) {
        this.platformAdapter = platformAdapter;
        this.lifecycle = lifecycle;
        this.itemListener = itemListener;
        this.blockListener = blockListener;
    }

    public void enable() {
        lifecycle.enable();
        platformAdapter.registerEventListeners(List.of(itemListener, blockListener));
    }

    public void disable() {
        lifecycle.disable();
    }
}
