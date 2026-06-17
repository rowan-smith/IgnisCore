package dev.rono.igniscore.loader;

import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.loader.support.TestExtensionJarBuilder;
import dev.rono.igniscore.support.TestIgnisCore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemExtensionLoaderTest {
    @TempDir
    Path tempDir;

    private ItemExtensionLoader loader;
    private File itemsDir;

    @BeforeEach
    void setUp() throws Exception {
        itemsDir = tempDir.resolve("items").toFile();
        itemsDir.mkdirs();
        var host = new dev.rono.igniscore.common.runtime.IgnisRuntimeHost() {
            @Override
            public java.util.logging.Logger getLogger() {
                return java.util.logging.Logger.getLogger("test");
            }

            @Override
            public Path getDataDirectory() {
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
        var engine = new ExtensionLoadEngine(host, TestIgnisCore.newStrategyRegistry(), null);
        loader = new ItemExtensionLoader(engine, new ExtensionResourceProvider());
    }

    @Test
    void loadFreshAndCommitLoadedManageItemExtensions() throws Exception {
        TestExtensionJarBuilder.writeItemJar(itemsDir, "grenade.jar");

        var fresh = loader.loadFresh();
        assertEquals(1, fresh.size());
        assertTrue(loader.getLoadedExtensions().isEmpty());

        loader.commitLoaded(fresh);
        assertEquals(1, loader.getLoadedExtensions().size());
        assertEquals("testitem", loader.getLoadedExtensions().getFirst().getDefinition().getId());
    }

    @Test
    void unloadAllClearsCommittedExtensions() throws Exception {
        TestExtensionJarBuilder.writeItemJar(itemsDir, "grenade.jar");
        loader.commitLoaded(loader.loadFresh());

        loader.unloadAll();

        assertTrue(loader.getLoadedExtensions().isEmpty());
    }
}
