package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.support.TestIgnisCore;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.support.TestExtensionJarBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;
import java.net.URLClassLoader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionJarSupportTest {
    @TempDir
    Path tempDir;

    @Test
    void readsManifestAndConfigFromJar() throws Exception {
        File jarFile = TestExtensionJarBuilder.writeBlockJar(tempDir.toFile(), "test-block.jar");

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        Map<String, Object> config = ExtensionJarSupport.readConfig(jarFile);

        assertEquals("test-block", manifest.getId());
        assertEquals("testblock", config.get("id"));
        assertEquals("test-block", DefinitionParser.parseStrategyDescriptor(manifest).getId());
    }

    @Test
    void loadsBlockStrategyFromJar() throws Exception {
        File jarFile = TestExtensionJarBuilder.writeBlockJar(tempDir.toFile(), "test-block.jar");
        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IgnisStrategy strategy = ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    nullContext(),
                    registry,
                    descriptor,
                    ExtensionKind.BLOCK
            );

            assertInstanceOf(dev.rono.igniscore.api.strategy.IgnisBlockStrategy.class, strategy);
            assertTrue(registry.isRegistered("test-block"));
            assertEquals("Test Block", registry.get("test-block").descriptor().getName());
        }
    }

    @Test
    void loadsItemStrategyFromJar() throws Exception {
        File jarFile = TestExtensionJarBuilder.writeItemJar(tempDir.toFile(), "test-item.jar");
        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "item-extension.yml",
                input -> ExtensionManifest.fromStream(input, "item-extension.yml"));
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IgnisStrategy strategy = ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    nullContext(),
                    registry,
                    descriptor,
                    ExtensionKind.ITEM
            );

            assertInstanceOf(dev.rono.igniscore.api.strategy.IgnisItemStrategy.class, strategy);
            assertTrue(registry.isRegistered("test-item"));
        }
    }

    @Test
    void rejectsWrongStrategyTypeForExtensionKind() throws Exception {
        File jarFile = TestExtensionJarBuilder.writeBlockJar(tempDir.toFile(), "test-block.jar");
        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IllegalStateException error = assertThrows(IllegalStateException.class, () -> ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    nullContext(),
                    registry,
                    descriptor,
                    ExtensionKind.ITEM
            ));
            assertEquals(manifest.getStrategyClass() + " must implement IgnisItemStrategy", error.getMessage());
        }
    }

    @Test
    void failsWhenManifestOrConfigMissing() throws Exception {
        File emptyJar = new File(tempDir.toFile(), "empty.jar");
        try (var jar = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(emptyJar))) {
            jar.putNextEntry(new java.util.jar.JarEntry("README.txt"));
            jar.write("noop".getBytes());
            jar.closeEntry();
        }

        IllegalStateException missingManifest = assertThrows(IllegalStateException.class,
                () -> ExtensionJarSupport.readManifest(emptyJar, "block-extension.yml",
                        input -> ExtensionManifest.fromStream(input, "block-extension.yml")));
        assertEquals("Missing block-extension.yml in empty.jar", missingManifest.getMessage());

        File manifestOnlyJar = TestExtensionJarBuilder.writeBlockJar(tempDir.toFile(), "manifest-only.jar");
        try (var jar = new java.util.jar.JarFile(manifestOnlyJar)) {
            var entries = jar.stream().map(java.util.jar.JarEntry::getName).toList();
            assertTrue(entries.contains("block-extension.yml"));
        }
    }

    @Test
    void parsesBlockDefinitionFromLoadedConfig() throws Exception {
        File jarFile = TestExtensionJarBuilder.writeBlockJar(tempDir.toFile(), "test-block.jar");
        Map<String, Object> config = ExtensionJarSupport.readConfig(jarFile);

        BlockDefinition blockDefinition = ExtensionKind.BLOCK.parseBlock(config, "fallback", 10055, "test-block");

        assertEquals("testblock", blockDefinition.getId());
        assertEquals(10055, blockDefinition.getCustomModelData());
        assertEquals("test-block", blockDefinition.getExtensionId());
    }

    @Test
    void failsWhenConfigYamlMissing() throws Exception {
        File jarFile = new File(tempDir.toFile(), "no-config.jar");
        try (var jar = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(jarFile))) {
            jar.putNextEntry(new java.util.jar.JarEntry("block-extension.yml"));
            jar.write("id: test-block".getBytes());
            jar.closeEntry();
        }

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> ExtensionJarSupport.readConfig(jarFile));
        assertEquals("Missing config.yml in no-config.jar", error.getMessage());
    }

    private IgnisStrategyContext nullContext() {
        return new IgnisStrategyContext(null, null, null, null, dev.rono.igniscore.support.NoopExtensionSupport.INSTANCE);
    }
}
