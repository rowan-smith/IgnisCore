package dev.rono.igniscore.sponge.runtime;

import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.sponge.SpongePluginContext;
import dev.rono.igniscore.sponge.SpongePluginHost;
import org.spongepowered.plugin.PluginContainer;

import java.nio.file.Path;
import java.util.logging.Logger;

public final class SpongeRuntimeHost implements IgnisRuntimeHost {
    private final SpongePluginHost plugin;
    private final PluginContainer container;
    private final Path dataDirectory;
    private final Logger logger;
    private final SpongePluginContext pluginContext;

    public SpongeRuntimeHost(SpongePluginHost plugin,
                             PluginContainer container,
                             Path dataDirectory,
                             Logger logger,
                             SpongePluginContext pluginContext) {
        this.plugin = plugin;
        this.container = container;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.pluginContext = pluginContext;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Path getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public ClassLoader getExtensionParentClassLoader() {
        return plugin.hostClass().getClassLoader();
    }

    @Override
    public void debug(String message) {
        pluginContext.debug(message);
    }
}
