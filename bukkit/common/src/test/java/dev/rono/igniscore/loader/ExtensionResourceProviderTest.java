package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.support.TestExtensionJarBuilder;
import dev.rono.igniscore.support.TestDefinitions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ExtensionResourceProviderTest {
    @TempDir
    Path tempDir;

    @Test
    void resolvesBlockAndItemTexturesFromRegisteredExtensions() throws Exception {
        ExtensionResourceProvider provider = new ExtensionResourceProvider();
        File blockJar = TestExtensionJarBuilder.writeBlockJar(tempDir.toFile(), "block.jar");
        File itemJar = TestExtensionJarBuilder.writeItemJar(tempDir.toFile(), "item.jar");

        try (URLClassLoader blockLoader = ExtensionJarSupport.createClassLoader(blockJar, getClass().getClassLoader());
             URLClassLoader itemLoader = ExtensionJarSupport.createClassLoader(itemJar, getClass().getClassLoader())) {
            BlockDefinition block = TestDefinitions.block("testblock");
            ItemDefinition item = TestDefinitions.item("testitem");

            provider.setBlockExtensions(java.util.List.of(TestDefinitions.loadedBlock(block, blockLoader)));
            provider.setItemExtensions(java.util.List.of(TestDefinitions.loadedItem(item, itemLoader)));

            assertNull(provider.getBlockTextureStream(block, "missing.png"));
            assertNull(provider.getItemTextureStream(item, "missing.png"));
        }
    }

    @Test
    void opensTexturesFromPrefixedAndRootPaths() throws Exception {
        ExtensionResourceProvider provider = new ExtensionResourceProvider();
        Path jarPath = tempDir.resolve("textures.jar");
        try (var jar = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(jarPath.toFile()))) {
            jar.putNextEntry(new java.util.jar.JarEntry("textures/icon.png"));
            jar.write(new byte[]{1, 2, 3});
            jar.closeEntry();
            jar.putNextEntry(new java.util.jar.JarEntry("fallback.png"));
            jar.write(new byte[]{4, 5});
            jar.closeEntry();
        }

        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[]{jarPath.toUri().toURL()},
                ClassLoader.getPlatformClassLoader())) {
            BlockDefinition block = TestDefinitions.block("textured");
            provider.setBlockExtensions(java.util.List.of(TestDefinitions.loadedBlock(block, classLoader)));

            try (InputStream prefixed = provider.getBlockTextureStream(block, "icon.png")) {
                assertNotNull(prefixed);
            }

            try (InputStream root = provider.getBlockTextureStream(block, "fallback.png")) {
                assertNotNull(root);
            }
        }
    }
}
