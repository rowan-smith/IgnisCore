package dev.rono.igniscore.sponge.runtime;

import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.sponge.SpongePluginHost;
import org.spongepowered.plugin.PluginContainer;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class SpongeRuntimeHost implements IgnisRuntimeHost {
    private final SpongePluginHost plugin;
    private final PluginContainer container;
    private final Path dataDirectory;
    private final Logger logger;

    public SpongeRuntimeHost(SpongePluginHost plugin,
                             PluginContainer container,
                             Path dataDirectory,
                             Logger logger) {
        this.plugin = plugin;
        this.container = container;
        this.dataDirectory = dataDirectory;
        this.logger = logger;
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
    public InputStream openBundledResource(String resourcePath) {
        return plugin.hostClass().getClassLoader().getResourceAsStream(resourcePath);
    }

    @Override
    public URI getDeploymentLocation() {
        try {
            return plugin.hostClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (java.net.URISyntaxException e) {
            throw new IllegalStateException("Could not resolve plugin deployment location", e);
        }
    }

    @Override
    public ClassLoader getExtensionParentClassLoader() {
        return plugin.hostClass().getClassLoader();
    }

    @Override
    public void debug(String message) {
        logger.info("[DEBUG] " + message);
    }
}
