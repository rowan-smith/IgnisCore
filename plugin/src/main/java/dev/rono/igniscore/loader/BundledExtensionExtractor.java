package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.BundledExtensions;
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
        extractBundled("bundled/blocks", destination, BundledExtensions.BLOCK_JARS);
    }

    public void extractBundledItems(File destination) {
        extractBundled("bundled/items", destination, java.util.List.of());
    }

    private void extractBundled(String resourceFolder, File destination, java.util.List<String> jarNames) {
        if (!destination.exists() && !destination.mkdirs()) {
            plugin.getLogger().warning("Could not create extension folder at " + destination.getAbsolutePath());
            return;
        }

        for (String jarName : jarNames) {
            String resourcePath = resourceFolder + "/" + jarName;
            File target = new File(destination, jarName);
            if (target.exists()) {
                continue;
            }

            try (InputStream inputStream = plugin.getResource(resourcePath)) {
                if (inputStream == null) {
                    continue;
                }
                Files.copy(inputStream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("Extracted bundled extension " + jarName + " to " + destination.getName() + "/");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to extract bundled extension " + jarName + ": " + e.getMessage());
            }
        }
    }
}
