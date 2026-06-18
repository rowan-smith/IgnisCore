package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.core.ExtensionLoadResult;
import dev.rono.igniscore.core.ExtensionReloadScope;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Singleton
public class SpongeExtensionReloadService {
    private final SpongePluginContext pluginContext;
    private final ExtensionBootstrap extensionBootstrap;
    private final PlatformAdapter platformAdapter;
    private final AtomicBoolean reloadInProgress = new AtomicBoolean();

    @Inject
    public SpongeExtensionReloadService(SpongePluginContext pluginContext,
                                        ExtensionBootstrap extensionBootstrap,
                                        PlatformAdapter platformAdapter) {
        this.pluginContext = pluginContext;
        this.extensionBootstrap = extensionBootstrap;
        this.platformAdapter = platformAdapter;
    }

    public void reloadAsync(ExtensionReloadScope scope,
                            Object sender,
                            String inProgressMessage,
                            String successMessage,
                            Runnable onSuccess) {
        if (!reloadInProgress.compareAndSet(false, true)) {
            if (sender != null) {
                platformAdapter.sendMessage(sender, pluginContext.message("<red>A reload is already in progress."));
            }
            return;
        }

        if (sender != null && inProgressMessage != null) {
            platformAdapter.sendMessage(sender, pluginContext.message(inProgressMessage));
        }

        extensionBootstrap.prepareForReload(scope);
        Executor asyncExecutor = asyncExecutor();
        asyncExecutor.execute(() -> {
            try {
                ExtensionLoadResult result = extensionBootstrap.loadFresh(scope);
                platformAdapter.getScheduler().runGlobal(() -> {
                    try {
                        extensionBootstrap.commitReload(scope, result);
                        if (sender != null && successMessage != null) {
                            platformAdapter.sendMessage(sender, pluginContext.message(successMessage));
                        }
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    } finally {
                        reloadInProgress.set(false);
                    }
                });
            } catch (RuntimeException error) {
                platformAdapter.getScheduler().runGlobal(() -> {
                    reloadInProgress.set(false);
                    if (sender != null) {
                        platformAdapter.sendMessage(sender, pluginContext.message(
                                "<red>Reload failed: " + error.getMessage()));
                    }
                });
            }
        });
    }

    private Executor asyncExecutor() {
        if (platformAdapter instanceof SpongePlatformAdapter spongeAdapter) {
            return spongeAdapter.game().server().scheduler().executor(spongeAdapter.container());
        }
        return Runnable::run;
    }
}
