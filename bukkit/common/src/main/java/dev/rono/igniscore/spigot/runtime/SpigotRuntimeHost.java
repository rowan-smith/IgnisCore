package dev.rono.igniscore.spigot.runtime;

import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class SpigotRuntimeHost implements IgnisRuntimeHost {
    private final JavaPlugin plugin;
    private final IgnisPluginContext pluginContext;

    public SpigotRuntimeHost(JavaPlugin plugin, IgnisPluginContext pluginContext) {
        this.plugin = plugin;
        this.pluginContext = pluginContext;
    }

    @Override
    public Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public Path getDataDirectory() {
        return plugin.getDataFolder().toPath();
    }

    @Override
    public InputStream openBundledResource(String resourcePath) {
        return plugin.getResource(resourcePath);
    }

    @Override
    public URI getDeploymentLocation() {
        try {
            return plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (java.net.URISyntaxException e) {
            throw new IllegalStateException("Could not resolve plugin deployment location", e);
        }
    }

    @Override
    public ClassLoader getExtensionParentClassLoader() {
        return plugin.getClass().getClassLoader();
    }

    @Override
    public void debug(String message) {
        pluginContext.debug(message);
    }
}
