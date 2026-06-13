package dev.rono.igniscore.folia.support;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

@SuppressWarnings({"deprecation", "removal"})
public class FoliaTestPlugin extends JavaPlugin {
    public FoliaTestPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }
}
