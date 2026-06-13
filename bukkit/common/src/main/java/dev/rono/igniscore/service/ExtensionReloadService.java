package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.core.ExtensionLoadResult;
import dev.rono.igniscore.core.ExtensionReloadScope;
import dev.rono.igniscore.platform.PlatformHooks;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

@Singleton
public class ExtensionReloadService {
    private final IgnisPluginContext pluginContext;
    private final ExtensionBootstrap extensionBootstrap;
    private final PlatformHooks platformHooks;

    @Inject
    public ExtensionReloadService(IgnisPluginContext pluginContext,
                                  ExtensionBootstrap extensionBootstrap,
                                  PlatformHooks platformHooks) {
        this.pluginContext = pluginContext;
        this.extensionBootstrap = extensionBootstrap;
        this.platformHooks = platformHooks;
    }

    public void reloadAsync(ExtensionReloadScope scope,
                            CommandSender sender,
                            String inProgressMessage,
                            String successMessage,
                            Runnable onSuccess) {
        if (sender != null && inProgressMessage != null) {
            platformHooks.sendMessage(sender, pluginContext.message(inProgressMessage));
        }

        var plugin = pluginContext.plugin();
        extensionBootstrap.prepareForReload(scope);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            ExtensionLoadResult result = extensionBootstrap.loadFresh(scope);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                extensionBootstrap.commitReload(scope, result);
                if (sender != null && successMessage != null) {
                    platformHooks.sendMessage(sender, pluginContext.message(successMessage));
                }
                if (onSuccess != null) {
                    onSuccess.run();
                }
            });
        });
    }

    public void reloadAsync(ExtensionReloadScope scope, Consumer<CommandSender> onSuccess, CommandSender sender) {
        reloadAsync(scope, sender,
                "<yellow>Reloading extensions...",
                "<green>Extension reload complete.",
                onSuccess != null ? () -> onSuccess.accept(sender) : null);
    }
}
