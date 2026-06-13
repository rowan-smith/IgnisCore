package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.loader.support.BundledExtensionJarFactory;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ExtensionDescriptorIntegrationTest {
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
            "grenade",
            "detonator"
    })
    void strategyDescriptorMatchesExtensionManifest(String moduleName) throws Exception {
        String category = Files.exists(Path.of("..", "..", "..", "extensions", "items", moduleName)) ? "items" : "blocks";
        String manifestName = category.equals("blocks") ? "block-extension.yml" : "item-extension.yml";
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, category, moduleName);

        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, manifestName,
                input -> ExtensionManifest.fromStream(input, manifestName));

        String strategyId = DefinitionParser.parseStrategyDescriptor(manifest).getId();

        assertFalse(strategyId.isBlank());
        assertEquals(manifest.getId(), strategyId);
        assertEquals(moduleName, manifest.getId());
    }
}
