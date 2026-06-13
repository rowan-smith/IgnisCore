package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.support.TestIgnisCore;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.support.BundledExtensionJarFactory;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockStrategyProfileIntegrationTest {
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
            "signal-charge"
    })
    void bundledStrategiesExposeProfilesForTheirDefinitions(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "blocks", moduleName);
        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        Map<String, Object> config = ExtensionJarSupport.readConfig(jarFile);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        BlockDefinition definition = ExtensionKind.BLOCK.parseBlock(config, manifest.getId(), 10001, manifest.getId());
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

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

            assertTrue(strategy instanceof IgnisBlockStrategy);
            StrategyProfile profile = ((IgnisBlockStrategy) strategy).profile(definition);
            assertNotNull(profile);
            assertTrue(profile.getDefaultFuse() > 0);
            assertTrue(profile.getDefaultRadius() >= 0.0);
        }
    }
}
