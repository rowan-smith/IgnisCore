package dev.rono.igniscore.sponge.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.sponge.IgnisSpongePlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class SpongeBundledExtensionExtractor {
    private final IgnisSpongePlugin plugin;
    private final PlatformAdapter platformAdapter;

    @Inject
    public SpongeBundledExtensionExtractor(IgnisSpongePlugin plugin, PlatformAdapter platformAdapter) {
        this.plugin = plugin;
        this.platformAdapter = platformAdapter;
    }

    public void extractBundled(String resourcePrefix, File destination) {
        if (!destination.exists() && !destination.mkdirs()) {
            platformAdapter.getLogger().warning("Could not create extension folder at " + destination.getAbsolutePath());
            return;
        }

        for (String resourcePath : listBundledResources(resourcePrefix)) {
            String jarName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            File target = new File(destination, jarName);

            try (InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (inputStream == null) {
                    continue;
                }
                Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                platformAdapter.getLogger().warning("Failed to extract bundled extension " + jarName + ": " + e.getMessage());
            }
        }
    }

    private List<String> listBundledResources(String resourcePrefix) {
        String normalizedPrefix = resourcePrefix.endsWith("/") ? resourcePrefix : resourcePrefix + "/";
        List<String> resources = new ArrayList<>();

        try {
            URI location = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            File pluginJar = new File(location);
            if (!pluginJar.isFile()) {
                return resources;
            }

            try (FileSystem fileSystem = FileSystems.newFileSystem(pluginJar.toPath(), (ClassLoader) null)) {
                Path bundledRoot = fileSystem.getPath("/" + normalizedPrefix);
                if (!Files.isDirectory(bundledRoot)) {
                    return resources;
                }

                try (Stream<Path> entries = Files.list(bundledRoot)) {
                    entries.filter(path -> path.getFileName().toString().endsWith(".jar"))
                            .forEach(path -> resources.add(normalizedPrefix + path.getFileName()));
                }
            }
        } catch (Exception e) {
            platformAdapter.getLogger().warning("Could not enumerate bundled extensions under " + resourcePrefix
                    + ": " + e.getMessage());
        }

        return resources;
    }
}
