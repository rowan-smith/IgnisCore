package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Singleton
public class BundledExtensionExtractor {
    private final Main plugin;

    @Inject
    public BundledExtensionExtractor(Main plugin) {
        this.plugin = plugin;
    }

    public void extractBundledBlocks(File destination) {
        extractBundled("bundled/blocks", destination);
    }

    public void extractBundledItems(File destination) {
        extractBundled("bundled/items", destination);
    }

    private void extractBundled(String resourceFolder, File destination) {
        if (!destination.exists() && !destination.mkdirs()) {
            plugin.getLogger().warning("Could not create extension folder at " + destination.getAbsolutePath());
            return;
        }

        for (String resourcePath : listBundledResources(resourceFolder)) {
            if (!resourcePath.endsWith(".jar")) {
                continue;
            }

            String fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            File target = new File(destination, fileName);
            if (target.exists()) {
                continue;
            }

            try (InputStream inputStream = plugin.getResource(resourcePath)) {
                if (inputStream == null) {
                    continue;
                }
                Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("Extracted bundled extension " + fileName + " to " + destination.getName() + "/");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to extract bundled extension " + fileName + ": " + e.getMessage());
            }
        }
    }

    private java.util.List<String> listBundledResources(String folder) {
        java.util.List<String> resources = new java.util.ArrayList<>();
        for (String fileName : defaultBundledNames(folder)) {
            String path = folder + "/" + fileName;
            if (plugin.getResource(path) != null) {
                resources.add(path);
            }
        }
        return resources;
    }

    private java.util.List<String> defaultBundledNames(String folder) {
        if ("bundled/blocks".equals(folder)) {
            return java.util.List.of(
                    "nuclear-block.jar",
                    "wormhole-block.jar",
                    "phantom-block.jar",
                    "erupting-block.jar",
                    "mimic-block.jar",
                    "tunneling-block.jar",
                    "entity-block.jar"
            );
        }
        return java.util.List.of();
    }
}
