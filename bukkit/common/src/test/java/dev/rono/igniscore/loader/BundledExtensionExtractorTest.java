package dev.rono.igniscore.loader;

import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.testsupport.CommonTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BundledExtensionExtractorTest {
    @TempDir
    Path tempDir;

    @Test
    void extractAllWritesBundledJarsToDataDirectory() throws Exception {
        Path pluginJar = tempDir.resolve("plugin.jar");
        writePluginJar(pluginJar, "bundled/blocks/demo.jar", "block-content".getBytes());
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);

        var host = hostFor(pluginJar, dataDir);
        new BundledExtensionExtractor(host).extractAll();

        File extracted = dataDir.resolve("blocks").resolve("demo.jar").toFile();
        assertTrue(extracted.isFile());
        assertEquals("block-content", Files.readString(extracted.toPath()));
    }

    @Test
    void extractAllSkipsUnchangedJar() throws Exception {
        Path pluginJar = tempDir.resolve("plugin.jar");
        writePluginJar(pluginJar, "bundled/blocks/demo.jar", "block-content".getBytes());
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        var host = hostFor(pluginJar, dataDir);
        var extractor = new BundledExtensionExtractor(host);

        extractor.extractAll();
        long firstModified = dataDir.resolve("blocks").resolve("demo.jar").toFile().lastModified();

        Thread.sleep(5);
        extractor.extractAll();
        long secondModified = dataDir.resolve("blocks").resolve("demo.jar").toFile().lastModified();

        assertEquals(firstModified, secondModified);
    }

    @Test
    void extractAllReplacesChangedJar() throws Exception {
        Path pluginJar = tempDir.resolve("plugin.jar");
        writePluginJar(pluginJar, "bundled/blocks/demo.jar", "version-one".getBytes());
        Path dataDir = tempDir.resolve("data");
        Files.createDirectories(dataDir);
        var host = hostFor(pluginJar, dataDir);
        var extractor = new BundledExtensionExtractor(host);
        extractor.extractAll();

        writePluginJar(pluginJar, "bundled/blocks/demo.jar", "version-two".getBytes());
        extractor.extractAll();

        assertEquals("version-two", Files.readString(dataDir.resolve("blocks").resolve("demo.jar")));
    }

    private static IgnisRuntimeHost hostFor(Path pluginJar, Path dataDir) {
        return new IgnisRuntimeHost() {
            @Override
            public java.util.logging.Logger getLogger() {
                return java.util.logging.Logger.getLogger("test");
            }

            @Override
            public Path getDataDirectory() {
                return dataDir;
            }

            @Override
            public java.io.InputStream openBundledResource(String resourcePath) {
                return null;
            }

            @Override
            public java.net.URI getDeploymentLocation() {
                return pluginJar.toUri();
            }

            @Override
            public ClassLoader getExtensionParentClassLoader() {
                return BundledExtensionExtractorTest.class.getClassLoader();
            }

            @Override
            public void debug(String message) {
            }
        };
    }

    private static void writePluginJar(Path pluginJar, String entryName, byte[] content) throws Exception {
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(pluginJar.toFile()))) {
            jar.putNextEntry(new JarEntry(entryName));
            jar.write(content);
            jar.closeEntry();
        }
    }
}
