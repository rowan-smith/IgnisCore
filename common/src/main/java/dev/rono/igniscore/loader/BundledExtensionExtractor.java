package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private final IgnisRuntimeHost host;

    @Inject
    public BundledExtensionExtractor(IgnisRuntimeHost host) {
        this.host = host;
    }

    public void extractBundled(String resourcePrefix, File destination) {
        if (!destination.exists() && !destination.mkdirs()) {
            host.getLogger().warning("Could not create extension folder at " + destination.getAbsolutePath());
            return;
        }

        for (String resourcePath : listBundledResources(resourcePrefix)) {
            extractResource(resourcePath, destination);
        }
    }

    public void extractAll() {
        File blocksDir = host.getDataDirectory().resolve("blocks").toFile();
        File itemsDir = host.getDataDirectory().resolve("items").toFile();
        if (!blocksDir.exists() && !blocksDir.mkdirs()) {
            host.getLogger().warning("Could not create blocks extension folder at " + blocksDir.getAbsolutePath());
        }
        if (!itemsDir.exists() && !itemsDir.mkdirs()) {
            host.getLogger().warning("Could not create items extension folder at " + itemsDir.getAbsolutePath());
        }

        try {
            Path pluginJar = Path.of(host.getDeploymentLocation());
            if (!Files.isRegularFile(pluginJar)) {
                return;
            }

            try (FileSystem fileSystem = FileSystems.newFileSystem(pluginJar, (ClassLoader) null)) {
                extractPrefix(fileSystem, "bundled/blocks/", blocksDir);
                extractPrefix(fileSystem, "bundled/items/", itemsDir);
            }
        } catch (Exception e) {
            host.getLogger().warning("Could not extract bundled extensions: " + e.getMessage());
        }
    }

    private void extractPrefix(FileSystem fileSystem, String normalizedPrefix, File destination) throws IOException {
        Path bundledRoot = fileSystem.getPath("/" + normalizedPrefix);
        if (!Files.isDirectory(bundledRoot)) {
            return;
        }

        try (Stream<Path> entries = Files.list(bundledRoot)) {
            entries.filter(path -> path.getFileName().toString().endsWith(".jar"))
                    .forEach(path -> {
                        String jarName = path.getFileName().toString();
                        File target = new File(destination, jarName);
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            host.debug("Extracted bundled extension " + jarName + " to " + destination.getName() + "/");
                        } catch (IOException e) {
                            host.getLogger().warning("Failed to extract bundled extension " + jarName + ": " + e.getMessage());
                        }
                    });
        }
    }

    private void extractResource(String resourcePath, File destination) {
        String jarName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        File target = new File(destination, jarName);

        try (InputStream inputStream = host.openBundledResource(resourcePath)) {
            if (inputStream == null) {
                return;
            }
            Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
            host.debug("Extracted bundled extension " + jarName + " to " + destination.getName() + "/");
        } catch (IOException e) {
            host.getLogger().warning("Failed to extract bundled extension " + jarName + ": " + e.getMessage());
        }
    }

    private List<String> listBundledResources(String resourcePrefix) {
        String normalizedPrefix = resourcePrefix.endsWith("/") ? resourcePrefix : resourcePrefix + "/";
        List<String> resources = new ArrayList<>();

        try {
            Path pluginJar = Path.of(host.getDeploymentLocation());
            if (!Files.isRegularFile(pluginJar)) {
                return resources;
            }

            // Use an isolated file-system view so we never close the plugin JAR that the host
            // class loader still has open (JarFile.close() would break class loading).
            try (FileSystem fileSystem = FileSystems.newFileSystem(pluginJar, (ClassLoader) null)) {
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
            host.getLogger().warning("Could not enumerate bundled extensions under " + resourcePrefix
                    + ": " + e.getMessage());
        }

        return resources;
    }
}
