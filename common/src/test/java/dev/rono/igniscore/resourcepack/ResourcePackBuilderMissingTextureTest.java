package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.loader.ExtensionResourceProvider;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.testsupport.CommonTestSupport;
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
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcePackBuilderMissingTextureTest {
    @Test
    void buildPackContinuesWhenBlockTextureIsMissing(@TempDir Path tempDir) throws Exception {
        BlockDefinition block = new BlockDefinition(
                "cavity-tnt",
                "paper",
                "carrot_on_a_stick",
                Component.text("Cavity TNT"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "cavity-tnt",
                null,
                null,
                null,
                null);

        Path jarPath = writeBlockJar(tempDir, "cavity-tnt");
        ExtensionResourceProvider resourceProvider = new ExtensionResourceProvider();
        try (URLClassLoader classLoader = new URLClassLoader(
                new java.net.URL[]{jarPath.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            resourceProvider.setBlockExtensions(List.of(loadedBlockExtension(block, jarPath, classLoader)));

            ResourcePackBuilder builder = new ResourcePackBuilder(
                    CommonTestSupport.runtimeHost(tempDir),
                    new ItemManager(),
                    resourceProvider);

            ResourcePackBuilder.PackResult result = builder.buildPack(Map.of(block.getId(), block), Map.of());

            assertNotNull(result.getFile());
            assertTrue(result.getFile().exists());
            assertFalse(result.getHash().isBlank());

            try (ZipFile zipFile = new ZipFile(result.getFile())) {
                assertNotNull(zipFile.getEntry("assets/igniscore/textures/block/cavity-tnt/side.png"));
                assertNotNull(zipFile.getEntry("assets/igniscore/textures/block/cavity-tnt/bottom.png"));
                assertTrue(zipFile.getEntry("assets/igniscore/textures/block/cavity-tnt/top.png") == null);
            }
        }
    }

    private static LoadedExtension<BlockDefinition> loadedBlockExtension(
            BlockDefinition definition, Path jarPath, URLClassLoader classLoader) throws Exception {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                ResourcePackBuilderMissingTextureTest.class.getResourceAsStream("/loader/block-extension.yml"),
                "block-extension.yml");
        return new LoadedExtension<>(
                manifest,
                jarPath.toFile(),
                classLoader,
                definition,
                new ExtensionResources(classLoader));
    }

    private static Path writeBlockJar(Path tempDir, String id) throws Exception {
        Path jarPath = tempDir.resolve(id + ".jar");
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath.toFile()))) {
            writeEntry(jar, "textures/side.png", "side-texture".getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, "textures/bottom.png", "bottom-texture".getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, "block-extension.yml", """
                    id: %s
                    name: Test
                    version: 1.0.0
                    api-version: 1
                    strategy: dev.example.Strategy
                    """.formatted(id).getBytes(StandardCharsets.UTF_8));
        }
        return jarPath;
    }

    private static void writeEntry(JarOutputStream jar, String name, byte[] bytes) throws Exception {
        jar.putNextEntry(new JarEntry(name));
        jar.write(bytes);
        jar.closeEntry();
    }
}
