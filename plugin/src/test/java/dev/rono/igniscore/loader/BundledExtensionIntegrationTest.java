package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.support.BundledExtensionJarFactory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundledExtensionIntegrationTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {
            "nuke-block",
            "wormhole-tnt-block",
            "phantom-tnt-block",
            "erupting-tnt-block",
            "mimic-tnt-block",
            "tunneling-tnt-block",
            "spider-storm-tnt-block",
            "signal-charge-block",
            "quarry-cache-block"
    })
    void loadsBundledBlockExtensionJar(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "blocks", moduleName);

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        BlockDefinition definition = ExtensionKind.BLOCK.parseBlock(config, manifest.getId(), 10001, manifest.getId());
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IgnisStrategy strategy = ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    new dev.rono.igniscore.api.strategy.IgnisStrategyContext(null, null, null, null,
                            dev.rono.igniscore.support.NoopExtensionSupport.INSTANCE),
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
    @ValueSource(strings = {"grenade-item", "detonator-item"})
    void loadsBundledItemExtensionJar(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "items", moduleName);

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "item-extension.yml",
                input -> ExtensionManifest.fromStream(input, "item-extension.yml"));
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        ItemDefinition definition = ExtensionKind.ITEM.parseItem(config, manifest.getId(), 20001, manifest.getId());
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            IgnisStrategy strategy = ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    new dev.rono.igniscore.api.strategy.IgnisStrategyContext(null, null, null, null,
                            dev.rono.igniscore.support.NoopExtensionSupport.INSTANCE),
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
        Path configPath = Path.of("..", "blocks", "nuke-block", "src", "main", "resources", "config.yml");
        String yaml = Files.readString(configPath, StandardCharsets.UTF_8);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader(yaml));

        BlockDefinition definition = DefinitionParser.parseBlock(config, "nuke", 10001, "nuke-block");

        assertEquals("nuke", definition.getId());
        assertEquals("nuke-block", definition.getExtensionId());
        assertEquals(160, definition.getCustomData().get("fuse"));
        assertEquals(30.0, definition.getCustomData().get("radius"));
    }

    static boolean nuclearConfigFixtureExists() {
        return Files.exists(Path.of("..", "blocks", "nuke-block", "src", "main", "resources", "config.yml"));
    }
}
