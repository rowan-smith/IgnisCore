package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.PlatformAdapter;

public final class IgnisSchedulerProvider implements Provider<IgnisScheduler> {
    private final PlatformAdapter platformAdapter;

    @Inject
    public IgnisSchedulerProvider(PlatformAdapter platformAdapter) {
        this.platformAdapter = platformAdapter;
    }

    @Override
    public IgnisScheduler get() {
        return platformAdapter.getScheduler();
    }
}
