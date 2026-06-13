package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExtensionResourceProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void resolvesBlockAndItemTextureStreams() throws Exception {
        ExtensionResourceProvider provider = new ExtensionResourceProvider();
        BlockDefinition block = sampleBlock("nuke");
        ItemDefinition item = sampleItem("grenade");

        Path blockJar = blockJar(block.getId(), "textures/top.png", "block-extension.yml");
        Path itemJar = itemJar(item.getId(), "textures/icon.png", "item-extension.yml");

        try (URLClassLoader blockLoader = new URLClassLoader(new java.net.URL[]{blockJar.toUri().toURL()},
                ClassLoader.getPlatformClassLoader());
             URLClassLoader itemLoader = new URLClassLoader(new java.net.URL[]{itemJar.toUri().toURL()},
                     ClassLoader.getPlatformClassLoader())) {
            provider.setBlockExtensions(List.of(loadedExtension(
                    block, "block-extension.yml", blockJar, blockLoader)));
            provider.setItemExtensions(List.of(loadedExtension(
                    item, "item-extension.yml", itemJar, itemLoader)));

            try (var blockStream = provider.getBlockTextureStream(block, "top.png");
                 var itemStream = provider.getItemTextureStream(item, "icon.png")) {
                assertNotNull(blockStream);
                assertNotNull(itemStream);
            }
            assertNull(provider.getBlockTextureStream(block, "missing.png"));
            assertNull(provider.getItemTextureStream(item, "missing.png"));
        }
    }

    private <D extends dev.rono.igniscore.api.model.ExtensionDefinition> LoadedExtension<D> loadedExtension(
            D definition,
            String manifestName,
            Path jarPath,
            URLClassLoader classLoader) throws Exception {
        ExtensionResources resources = new ExtensionResources(classLoader);
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                getClass().getResourceAsStream("/loader/" + manifestName),
                manifestName);
        return new LoadedExtension<>(manifest, jarPath.toFile(), classLoader, definition, resources);
    }

    private Path blockJar(String id, String resourcePath, String manifestName) throws Exception {
        return writeJar(id, resourcePath, manifestName, """
                id: %s
                name: Test
                version: 1.0.0
                api-version: 1
                strategy: dev.example.Strategy
                """.formatted(id));
    }

    private Path itemJar(String id, String resourcePath, String manifestName) throws Exception {
        return writeJar(id, resourcePath, manifestName, """
                id: %s
                name: Test
                version: 1.0.0
                api-version: 1
                strategy: dev.example.Strategy
                """.formatted(id));
    }

    private Path writeJar(String id, String resourcePath, String manifestName, String manifestYaml) throws Exception {
        Path jarPath = tempDir.resolve(id + ".jar");
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath.toFile()))) {
            writeEntry(jar, resourcePath, "texture".getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, manifestName, manifestYaml.getBytes(StandardCharsets.UTF_8));
        }
        return jarPath;
    }

    private static void writeEntry(JarOutputStream jar, String name, byte[] bytes) throws Exception {
        jar.putNextEntry(new JarEntry(name));
        jar.write(bytes);
        jar.closeEntry();
    }

    private static BlockDefinition sampleBlock(String id) {
        return new BlockDefinition(
                id, "paper", "carrot_on_a_stick", Component.text("Test"), List.of(),
                true, true, "top.png", "side.png", "bottom.png",
                Map.of(), Map.of(), Map.of(), Map.of(), 10001, false, false, false);
    }

    private static ItemDefinition sampleItem(String id) {
        return new ItemDefinition(
                id, "snowball", Component.text("Test"), List.of(),
                Map.of(), Map.of(), 20001, id, "icon.png");
    }
}
