package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.extension.BlockExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.model.BlockDefinition;

import java.io.File;
import java.net.URLClassLoader;

public final class LoadedBlockExtension {
    private final BlockExtensionManifest manifest;
    private final File jarFile;
    private final URLClassLoader classLoader;
    private final BlockDefinition blockDefinition;
    private final ExtensionResources resources;

    public LoadedBlockExtension(BlockExtensionManifest manifest,
                                File jarFile,
                                URLClassLoader classLoader,
                                BlockDefinition blockDefinition,
                                ExtensionResources resources) {
        this.manifest = manifest;
        this.jarFile = jarFile;
        this.classLoader = classLoader;
        this.blockDefinition = blockDefinition;
        this.resources = resources;
    }

    public BlockExtensionManifest getManifest() {
        return manifest;
    }

    public File getJarFile() {
        return jarFile;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public BlockDefinition getBlockDefinition() {
        return blockDefinition;
    }

    public ExtensionResources getResources() {
        return resources;
    }
}
