package dev.rono.igniscore.loader.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

public final class BundledExtensionJarFactory {
    private BundledExtensionJarFactory() {
    }

    public static Path extensionsRoot() throws IOException {
        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            Path extensions = current.resolve("extensions");
            if (Files.isDirectory(extensions)) {
                return extensions;
            }
            current = current.getParent();
        }
        throw new IOException("Could not locate extensions directory from working directory "
                + Path.of("").toAbsolutePath());
    }

    public static Path moduleDirectory(String category, String moduleName) throws IOException {
        return extensionsRoot().resolve(category).resolve(moduleName);
    }

    public static File buildFromModule(Path outputDirectory, String category, String moduleName) throws IOException {
        Path classesDirectory = moduleDirectory(category, moduleName).resolve("target/classes");

        if (!Files.isDirectory(classesDirectory)) {
            throw new IOException("Missing compiled classes for " + moduleName + " at " + classesDirectory);
        }

        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        File jarFile = outputDirectory.resolve(moduleName + ".jar").toFile();
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarFile))) {
            writeDirectory(jar, classesDirectory, classesDirectory);
        }
        return jarFile;
    }

    private static void writeDirectory(JarOutputStream jar, Path root, Path current) throws IOException {
        try (Stream<Path> paths = Files.walk(current)) {
            for (Path path : paths.filter(Files::isRegularFile).toList()) {
                String entryName = root.relativize(path).toString().replace('\\', '/');
                jar.putNextEntry(new JarEntry(entryName));
                try (InputStream inputStream = Files.newInputStream(path)) {
                    inputStream.transferTo(jar);
                }
                jar.closeEntry();
            }
        }
    }
}
