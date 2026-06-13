package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.ExtensionDefinition;

import java.io.File;
import java.net.URLClassLoader;

public final class LoadedExtension<D extends ExtensionDefinition> {
    private final ExtensionManifest manifest;
    private final File jarFile;
    private final URLClassLoader classLoader;
    private final D definition;
    private final ExtensionResources resources;

    public LoadedExtension(ExtensionManifest manifest,
                           File jarFile,
                           URLClassLoader classLoader,
                           D definition,
                           ExtensionResources resources) {
        this.manifest = manifest;
        this.jarFile = jarFile;
        this.classLoader = classLoader;
        this.definition = definition;
        this.resources = resources;
    }

    public ExtensionManifest getManifest() {
        return manifest;
    }

    public File getJarFile() {
        return jarFile;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public D getDefinition() {
        return definition;
    }

    public ExtensionResources getResources() {
        return resources;
    }
}
