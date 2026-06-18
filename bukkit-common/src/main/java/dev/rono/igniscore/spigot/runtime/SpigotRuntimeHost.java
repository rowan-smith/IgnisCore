package dev.rono.igniscore.spigot.runtime;

import dev.rono.igniscore.IgnisPluginContext;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import org.bukkit.plugin.java.JavaPlugin;

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
    public ClassLoader getExtensionParentClassLoader() {
        return plugin.getClass().getClassLoader();
    }

    @Override
    public void debug(String message) {
        pluginContext.debug(message);
    }
}
