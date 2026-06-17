package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.support.TestIgnisCore;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.support.BundledExtensionJarFactory;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockStrategyProfileIntegrationTest {
    @TempDir
    Path tempDir;

    @ParameterizedTest
    @ValueSource(strings = {
            "auto-sieve",
            "picnic-basket",
            "socket-lamp"
    })
    void placedUtilityStrategiesLoadWithoutFuseConfig(String moduleName) throws Exception {
        BlockDefinition definition = loadDefinition(moduleName);
        loadStrategy(moduleName);

        assertFalse(definition.getCustomData().containsKey("fuse"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "nuke",
            "wormhole-tnt",
            "phantom-tnt",
            "erupting-tnt",
            "mimic-tnt",
            "tunneling-tnt",
            "spider-storm-tnt"
    })
    void bundledExplosiveStrategiesDeclareFuseInConfig(String moduleName) throws Exception {
        BlockDefinition definition = loadDefinition(moduleName);
        IgnisStrategy strategy = loadStrategy(moduleName);

        assertTrue(strategy instanceof IgnisBlockStrategy);
        assertNotNull(definition);
        assertTrue(definition.getCustomData().containsKey("fuse"));
        assertTrue(StrategySupport.customInt(definition, "fuse", -1) >= 0);
        assertTrue(StrategySupport.customDouble(definition, "radius", -1.0) >= 0.0);
    }

    private BlockDefinition loadDefinition(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "blocks", moduleName);
        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        Map<String, Object> config = ExtensionJarSupport.readConfig(jarFile);
        return (BlockDefinition) ExtensionKind.BLOCK.parseDefinition(config, manifest.getId(), 10001, manifest.getId());
    }

    private IgnisStrategy loadStrategy(String moduleName) throws Exception {
        File jarFile = BundledExtensionJarFactory.buildFromModule(tempDir, "blocks", moduleName);
        ExtensionManifest manifest = ExtensionJarSupport.readManifest(jarFile, "block-extension.yml",
                input -> ExtensionManifest.fromStream(input, "block-extension.yml"));
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();

        try (URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, getClass().getClassLoader())) {
            return ExtensionJarSupport.loadStrategy(
                    classLoader,
                    manifest.getStrategyClass(),
                    TestIgnisCore.noopStrategyContext(),
                    registry,
                    descriptor,
                    ExtensionKind.BLOCK
            );
        }
    }
}
