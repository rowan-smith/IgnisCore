package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.extension.IgnisItemPlugin;
import dev.rono.igniscore.api.extension.ItemExtensionManifest;
import dev.rono.igniscore.model.ItemDefinition;

import java.io.File;
import java.net.URLClassLoader;

public final class LoadedItemExtension {
    private final ItemExtensionManifest manifest;
    private final File jarFile;
    private final URLClassLoader classLoader;
    private final IgnisItemPlugin plugin;
    private final ItemDefinition itemDefinition;
    private final ExtensionResources resources;

    public LoadedItemExtension(ItemExtensionManifest manifest,
                               File jarFile,
                               URLClassLoader classLoader,
                               IgnisItemPlugin plugin,
                               ItemDefinition itemDefinition,
                               ExtensionResources resources) {
        this.manifest = manifest;
        this.jarFile = jarFile;
        this.classLoader = classLoader;
        this.plugin = plugin;
        this.itemDefinition = itemDefinition;
        this.resources = resources;
    }

    public ItemExtensionManifest getManifest() {
        return manifest;
    }

    public File getJarFile() {
        return jarFile;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public IgnisItemPlugin getPlugin() {
        return plugin;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public ExtensionResources getResources() {
        return resources;
    }
}
