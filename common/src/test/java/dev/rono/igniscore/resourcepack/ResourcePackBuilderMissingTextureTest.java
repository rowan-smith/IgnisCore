package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void buildPackUsesMinecraftFallbackForMissingBlockFace(@TempDir Path tempDir) throws Exception {
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
                null,
                "minecraft:tnt");

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

            try (ZipFile zipFile = new ZipFile(result.getFile())) {
                assertNull(zipFile.getEntry("assets/igniscore/textures/block/cavity-tnt/top.png"));
                var entry = zipFile.getEntry("assets/igniscore/models/block/cavity-tnt.json");
                assertNotNull(entry);
                String blockModel = new String(zipFile.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
                assertTrue(blockModel.contains("\"top\": \"minecraft:block/tnt_top\""));
            }
        }
    }

    @Test
    void buildPackCopiesExtensionFallbackTextureForMissingBlockFace(@TempDir Path tempDir) throws Exception {
        BlockDefinition grenade = new BlockDefinition(
                "grenade",
                "paper",
                "carrot_on_a_stick",
                Component.text("Grenade"),
                List.of(),
                true,
                true,
                "grenade-top.png",
                "grenade-side.png",
                "grenade-bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                10002,
                false,
                false,
                false,
                "grenade",
                null,
                null,
                null,
                null,
                null);

        BlockDefinition remote = new BlockDefinition(
                "remote",
                "paper",
                "carrot_on_a_stick",
                Component.text("Remote"),
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
                10003,
                false,
                false,
                false,
                "remote",
                null,
                null,
                null,
                null,
                "grenade");

        Path grenadeJar = writeBlockJarWithTextures(tempDir, "grenade", "grenade-top.png", "grenade-side.png", "grenade-bottom.png");
        Path remoteJar = writeBlockJar(tempDir, "remote");
        ExtensionResourceProvider resourceProvider = new ExtensionResourceProvider();
        try (URLClassLoader grenadeLoader = new URLClassLoader(
                     new java.net.URL[]{grenadeJar.toUri().toURL()}, ClassLoader.getPlatformClassLoader());
             URLClassLoader remoteLoader = new URLClassLoader(
                     new java.net.URL[]{remoteJar.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            resourceProvider.setBlockExtensions(List.of(
                    loadedBlockExtension(grenade, grenadeJar, grenadeLoader),
                    loadedBlockExtension(remote, remoteJar, remoteLoader)));

            ResourcePackBuilder builder = new ResourcePackBuilder(
                    CommonTestSupport.runtimeHost(tempDir),
                    new ItemManager(),
                    resourceProvider);

            ResourcePackBuilder.PackResult result = builder.buildPack(
                    Map.of(grenade.getId(), grenade, remote.getId(), remote),
                    Map.of());

            try (ZipFile zipFile = new ZipFile(result.getFile())) {
                assertNotNull(zipFile.getEntry("assets/igniscore/textures/block/remote/top.png"));
            }
        }
    }

    @Test
    void buildPackUsesExtensionItemIconAsBlockFallback(@TempDir Path tempDir) throws Exception {
        ItemDefinition grenade = new ItemDefinition(
                "grenade",
                "snowball",
                Component.text("Grenade"),
                List.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                20001,
                "grenade",
                "grenade-icon.png",
                null);

        BlockDefinition remote = new BlockDefinition(
                "remote",
                "paper",
                "carrot_on_a_stick",
                Component.text("Remote"),
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
                10003,
                false,
                false,
                false,
                "remote",
                null,
                null,
                null,
                null,
                "grenade");

        Path grenadeJar = writeItemJar(tempDir, "grenade", "grenade-icon.png");
        Path remoteJar = writeBlockJar(tempDir, "remote");
        ExtensionResourceProvider resourceProvider = new ExtensionResourceProvider();
        try (URLClassLoader grenadeLoader = new URLClassLoader(
                     new java.net.URL[]{grenadeJar.toUri().toURL()}, ClassLoader.getPlatformClassLoader());
             URLClassLoader remoteLoader = new URLClassLoader(
                     new java.net.URL[]{remoteJar.toUri().toURL()}, ClassLoader.getPlatformClassLoader())) {
            resourceProvider.setItemExtensions(List.of(
                    loadedItemExtension(grenade, grenadeJar, grenadeLoader)));
            resourceProvider.setBlockExtensions(List.of(
                    loadedBlockExtension(remote, remoteJar, remoteLoader)));

            ResourcePackBuilder builder = new ResourcePackBuilder(
                    CommonTestSupport.runtimeHost(tempDir),
                    new ItemManager(),
                    resourceProvider);

            ResourcePackBuilder.PackResult result = builder.buildPack(
                    Map.of(remote.getId(), remote),
                    Map.of(grenade.getId(), grenade));

            try (ZipFile zipFile = new ZipFile(result.getFile())) {
                assertNotNull(zipFile.getEntry("assets/igniscore/textures/block/remote/top.png"));
            }
        }
    }

    private static LoadedExtension<ItemDefinition> loadedItemExtension(
            ItemDefinition definition, Path jarPath, URLClassLoader classLoader) throws Exception {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                ResourcePackBuilderMissingTextureTest.class.getResourceAsStream("/loader/item-extension.yml"),
                "item-extension.yml");
        return new LoadedExtension<>(
                manifest,
                jarPath.toFile(),
                classLoader,
                definition,
                new ExtensionResources(classLoader));
    }

    private static Path writeBlockJarWithTextures(Path tempDir, String id, String top, String side, String bottom) throws Exception {
        Path jarPath = tempDir.resolve(id + ".jar");
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath.toFile()))) {
            writeEntry(jar, "textures/" + top, "top-texture".getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, "textures/" + side, "side-texture".getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, "textures/" + bottom, "bottom-texture".getBytes(StandardCharsets.UTF_8));
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

    private static Path writeItemJar(Path tempDir, String id, String icon) throws Exception {
        Path jarPath = tempDir.resolve(id + ".jar");
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath.toFile()))) {
            writeEntry(jar, "textures/" + icon, "icon-texture".getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, "item-extension.yml", """
                    id: %s
                    name: Test
                    version: 1.0.0
                    api-version: 1
                    strategy: dev.example.Strategy
                    """.formatted(id).getBytes(StandardCharsets.UTF_8));
        }
        return jarPath;
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
