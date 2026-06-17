package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.support.TestIgnisCore;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.api.config.YamlDefinitions;
import dev.rono.igniscore.loader.support.BundledExtensionJarFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundledExtensionIntegrationTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {
            "nuke",
            "wormhole-tnt",
            "phantom-tnt",
            "erupting-tnt",
            "mimic-tnt",
            "tunneling-tnt",
            "spider-storm-tnt",
            "signal-charge",
            "quarry-cache"
    })
    void loadsBundledBlockExtensionJar(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "blocks", moduleName);

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        Map<String, Object> config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        BlockDefinition definition = (BlockDefinition) ExtensionKind.BLOCK.parseDefinition(config, manifest.getId(), 10001, manifest.getId());
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IgnisStrategy strategy = ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    TestIgnisCore.noopStrategyContext(),
                    registry,
                    descriptor,
                    ExtensionKind.BLOCK
            );

            assertNotNull(strategy);
            assertTrue(registry.isRegistered(descriptor.getId()));
            assertEquals(manifest.getId(), definition.getExtensionId());
            assertFalse(definition.getId().isBlank());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"grenade", "detonator"})
    void loadsBundledItemExtensionJar(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "items", moduleName);

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "item-extension.yml",
                input -> ExtensionManifest.fromStream(input, "item-extension.yml"));
        Map<String, Object> config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        ItemDefinition definition = (ItemDefinition) ExtensionKind.ITEM.parseDefinition(config, manifest.getId(), 20001, manifest.getId());
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IgnisStrategy strategy = ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    TestIgnisCore.noopStrategyContext(),
                    registry,
                    descriptor,
                    ExtensionKind.ITEM
            );

            assertNotNull(strategy);
            assertTrue(registry.isRegistered(descriptor.getId()));
            assertEquals(manifest.getId(), definition.getExtensionId());
        }
    }

    @Test
    @EnabledIf("nuclearConfigFixtureExists")
    void parsesRepositoryNuclearConfigFixture() throws Exception {
        Path configPath = BundledExtensionJarFactory.moduleDirectory("blocks", "nuke")
                .resolve("src/main/resources/config.yml");
        Map<String, Object> config = YamlDefinitions.loadMap(Files.newInputStream(configPath));

        BlockDefinition definition = DefinitionParser.parseBlock(config, "nuke", 10001, "nuke");

        assertEquals("nuke", definition.getId());
        assertEquals("nuke", definition.getExtensionId());
        assertEquals(160, definition.getCustomData().get("fuse"));
        assertEquals(30.0, definition.getCustomData().get("radius"));
    }

    static boolean nuclearConfigFixtureExists() {
        try {
            return Files.exists(BundledExtensionJarFactory.moduleDirectory("blocks", "nuke")
                    .resolve("src/main/resources/config.yml"));
        } catch (IOException ignored) {
            return false;
        }
    }
}
