package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Singleton
public class ContentPackLoader {
    private final Main plugin;
    private final List<LoadedContentPack> loadedPacks = new ArrayList<>();

    @Inject
    public ContentPackLoader(Main plugin) {
        this.plugin = plugin;
    }

    public List<LoadedContentPack> loadAll() {
        loadedPacks.clear();

        File packsFolder = new File(plugin.getDataFolder(), "packs");
        if (!packsFolder.exists() && !packsFolder.mkdirs()) {
            plugin.getLogger().warning("Could not create packs folder at " + packsFolder.getAbsolutePath());
            return List.copyOf(loadedPacks);
        }

        File[] entries = packsFolder.listFiles();
        if (entries == null || entries.length == 0) {
            plugin.getLogger().info("No content packs found in " + packsFolder.getAbsolutePath());
            return List.copyOf(loadedPacks);
        }

        for (File entry : entries) {
            try {
                if (entry.isDirectory()) {
                    loadedPacks.add(loadFolderPack(entry));
                } else if (entry.getName().endsWith(".zip")) {
                    loadedPacks.add(loadZipPack(entry));
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load content pack " + entry.getName() + ": " + e.getMessage());
            }
        }

        return List.copyOf(loadedPacks);
    }

    public List<LoadedContentPack> getLoadedPacks() {
        return List.copyOf(loadedPacks);
    }

    private LoadedContentPack loadFolderPack(File packFolder) {
        PackManifest manifest = PackManifest.fromFile(packFolder);
        plugin.getLogger().info("Loaded content pack '" + manifest.getName() + "' v" + manifest.getVersion()
                + " from folder " + packFolder.getName());
        return new LoadedContentPack(manifest, packFolder);
    }

    private LoadedContentPack loadZipPack(File zipFile) throws IOException {
        String packId = zipFile.getName().substring(0, zipFile.getName().length() - 4);
        File extractFolder = new File(plugin.getDataFolder(), "packs/.extracted/" + packId);
        if (extractFolder.exists()) {
            deleteDirectory(extractFolder);
        }
        Files.createDirectories(extractFolder.toPath());
        unzip(zipFile, extractFolder);

        PackManifest manifest = PackManifest.fromFile(extractFolder);
        plugin.getLogger().info("Loaded content pack '" + manifest.getName() + "' v" + manifest.getVersion()
                + " from zip " + zipFile.getName());
        return new LoadedContentPack(manifest, extractFolder);
    }

    private void unzip(File zipFile, File destination) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File outputFile = new File(destination, entry.getName());
                if (!outputFile.toPath().normalize().startsWith(destination.toPath().normalize())) {
                    throw new IOException("Zip entry escapes destination directory: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(outputFile.toPath());
                    continue;
                }

                Files.createDirectories(outputFile.getParentFile().toPath());
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    zipInputStream.transferTo(outputStream);
                }
            }
        }
    }

    private void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        try (var paths = Files.walk(directory.toPath())) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw e;
        }
    }
}
