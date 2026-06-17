package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.support.TestExtensionJarBuilder;
import dev.rono.igniscore.support.TestIgnisCore;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockExtensionLoaderTest {
    @TempDir
    Path tempDir;

    private BlockExtensionLoader loader;
    private ExtensionResourceProvider resourceProvider;
    private File blocksDir;

    @BeforeEach
    void setUp() throws Exception {
        blocksDir = tempDir.resolve("blocks").toFile();
        blocksDir.mkdirs();
        var host = new dev.rono.igniscore.common.runtime.IgnisRuntimeHost() {
            @Override
            public java.util.logging.Logger getLogger() {
                return java.util.logging.Logger.getLogger("test");
            }

            @Override
            public java.nio.file.Path getDataDirectory() {
                return tempDir;
            }

            @Override
            public java.io.InputStream openBundledResource(String resourcePath) {
                return null;
            }

            @Override
            public java.net.URI getDeploymentLocation() {
                return tempDir.toUri();
            }

            @Override
            public ClassLoader getExtensionParentClassLoader() {
                return getClass().getClassLoader();
            }

            @Override
            public void debug(String message) {
            }
        };
        var strategyRegistry = TestIgnisCore.newStrategyRegistry();
        var engine = new ExtensionLoadEngine(host, strategyRegistry, TestIgnisCore.noopStrategyContext(),
                TestIgnisCore.permissiveIntegrationRegistry());
        resourceProvider = new ExtensionResourceProvider();
        loader = new BlockExtensionLoader(engine, resourceProvider);
    }

    @Test
    void loadFreshDoesNotCommitUntilRequested() throws Exception {
        TestExtensionJarBuilder.writeBlockJar(blocksDir, "alpha.jar");

        var fresh = loader.loadFresh();

        assertEquals(1, fresh.size());
        assertTrue(loader.getLoadedExtensions().isEmpty());
    }

    @Test
    void commitLoadedReplacesPreviousExtensions() throws Exception {
        TestExtensionJarBuilder.writeBlockJar(blocksDir, "alpha.jar");
        var first = loader.loadFresh();
        loader.commitLoaded(first);
        loader.unloadAll();
        assertTrue(new File(blocksDir, "alpha.jar").delete());

        TestExtensionJarBuilder.writeBlockJar(blocksDir, "beta.jar");
        var second = loader.loadFresh();
        loader.commitLoaded(second);

        assertEquals(1, loader.getLoadedExtensions().size());
        BlockDefinition definition = loader.getLoadedExtensions().getFirst().getDefinition();
        assertEquals("testblock", definition.getId());
    }

    @Test
    void unloadAllClearsLoadedExtensions() throws Exception {
        TestExtensionJarBuilder.writeBlockJar(blocksDir, "alpha.jar");
        loader.commitLoaded(loader.loadFresh());

        loader.unloadAll();

        assertTrue(loader.getLoadedExtensions().isEmpty());
    }
}
