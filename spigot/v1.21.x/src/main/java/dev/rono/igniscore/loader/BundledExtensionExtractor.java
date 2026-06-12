package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;

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
public class BundledExtensionExtractor {
    private final Main plugin;

    @Inject
    public BundledExtensionExtractor(Main plugin) {
        this.plugin = plugin;
    }

    public void extractBundled(String resourcePrefix, File destination) {
        if (!destination.exists() && !destination.mkdirs()) {
            plugin.getLogger().warning("Could not create extension folder at " + destination.getAbsolutePath());
            return;
        }

        for (String resourcePath : listBundledResources(resourcePrefix)) {
            String jarName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            File target = new File(destination, jarName);

            try (InputStream inputStream = plugin.getResource(resourcePath)) {
                if (inputStream == null) {
                    continue;
                }
                Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.debug("Extracted bundled extension " + jarName + " to " + destination.getName() + "/");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to extract bundled extension " + jarName + ": " + e.getMessage());
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

            // Use an isolated file-system view so we never close the plugin JAR that Paper's
            // PluginClassLoader still has open (JarFile.close() would break class loading).
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
            plugin.getLogger().warning("Could not enumerate bundled extensions under " + resourcePrefix
                    + ": " + e.getMessage());
        }

        return resources;
    }
}
