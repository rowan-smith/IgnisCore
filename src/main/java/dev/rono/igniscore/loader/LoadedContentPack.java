package dev.rono.igniscore.loader;

import java.io.File;

public final class LoadedContentPack {
    private final PackManifest manifest;
    private final File rootFolder;

    public LoadedContentPack(PackManifest manifest, File rootFolder) {
        this.manifest = manifest;
        this.rootFolder = rootFolder;
    }

    public PackManifest getManifest() {
        return manifest;
    }

    public File getRootFolder() {
        return rootFolder;
    }

    public File getBlocksFolder() {
        return new File(rootFolder, "blocks");
    }
}
