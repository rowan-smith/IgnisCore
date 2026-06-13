package dev.rono.igniscore.support;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@SuppressWarnings({"deprecation", "removal"})
public class TestIgnisPlugin extends JavaPlugin {
    public TestIgnisPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }
}
