package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.loader.support.BundledExtensionJarFactory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ExtensionDescriptorIntegrationTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {
            "nuclear-block",
            "wormhole-block",
            "phantom-block",
            "erupting-block",
            "mimic-block",
            "tunneling-block",
            "entity-block",
            "signal-charge-block",
            "grenade-item",
            "detonator-item"
    })
    void strategyDescriptorMatchesConfigBehavior(String moduleName) throws Exception {
        String category = moduleName.endsWith("-item") ? "items" : "blocks";
        String manifestName = category.equals("blocks") ? "block-extension.yml" : "item-extension.yml";
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, category, moduleName);

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, manifestName,
                input -> ExtensionManifest.fromStream(input, manifestName));
        YamlConfiguration config = ExtensionJarSupport.readConfig(jarFile);

        String strategyId = DefinitionParser.parseStrategyDescriptor(config, manifest).getId();
        String configuredStrategy = config.getString("behavior.strategy");

        assertFalse(strategyId.isBlank());
        assertEquals(configuredStrategy, strategyId);
        assertEquals(manifest.getId(), moduleName);
    }
}
