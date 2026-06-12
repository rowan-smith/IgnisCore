package dev.rono.igniscore.loader.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class TestExtensionJarBuilder {
    private TestExtensionJarBuilder() {
    }

    public static File writeBlockJar(File directory, String jarName) throws IOException {
        return writeJar(directory, jarName, "block-extension.yml", blockManifest(), blockConfig(),
                TestBlockStrategy.class.getName());
    }

    public static File writeItemJar(File directory, String jarName) throws IOException {
        return writeJar(directory, jarName, "item-extension.yml", itemManifest(), itemConfig(),
                TestItemStrategy.class.getName());
    }

    private static File writeJar(File directory,
                                 String jarName,
                                 String manifestName,
                                 String manifestYaml,
                                 String configYaml,
                                 String strategyClassName) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Could not create directory " + directory);
        }

        File jarFile = new File(directory, jarName);
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarFile))) {
            writeEntry(jar, manifestName, manifestYaml.getBytes(StandardCharsets.UTF_8));
            writeEntry(jar, "config.yml", configYaml.getBytes(StandardCharsets.UTF_8));
            writeClass(jar, strategyClassName);
        }
        return jarFile;
    }

    private static void writeClass(JarOutputStream jar, String className) throws IOException {
        String resourcePath = className.replace('.', '/') + ".class";
        try (InputStream inputStream = TestExtensionJarBuilder.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Missing compiled test class " + resourcePath);
            }
            writeEntry(jar, resourcePath, inputStream.readAllBytes());
        }
    }

    private static void writeEntry(JarOutputStream jar, String name, byte[] bytes) throws IOException {
        jar.putNextEntry(new JarEntry(name));
        jar.write(bytes);
        jar.closeEntry();
    }

    private static String blockManifest() {
        return """
                id: test-block
                name: Test Block
                version: 1.0.0
                api-version: 1.0.0
                author: Tests
                strategy: %s
                """.formatted(TestBlockStrategy.class.getName());
    }

    private static String blockConfig() {
        return """
                id: testblock
                display:
                  title: "&aTest Block"
                block:
                  base_material: paper
                """;
    }

    private static String itemManifest() {
        return """
                id: test-item
                name: Test Item
                version: 1.0.0
                api-version: 1.0.0
                author: Tests
                strategy: %s
                """.formatted(TestItemStrategy.class.getName());
    }

    private static String itemConfig() {
        return """
                id: testitem
                display:
                  title: "&cTest Item"
                item:
                  base_material: snowball
                """;
    }

    public static InputStream manifestStream(String yaml) {
        return new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8));
    }
}
