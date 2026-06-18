package dev.rono.igniscore.loader.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BundledExtensionJarFactory {
    private BundledExtensionJarFactory() {
    }

    public static Path bundledRoot() throws IOException {
        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            Path bundled = current.resolve("bootstrap/bundled");
            if (Files.isDirectory(bundled)) {
                return bundled;
            }
            current = current.getParent();
        }
        throw new IOException("Could not locate bootstrap/bundled from working directory "
                + Path.of("").toAbsolutePath());
    }

    public static boolean bundledJarExists(String category, String moduleName) {
        try {
            return Files.isRegularFile(bundledRoot().resolve(category).resolve(moduleName + ".jar"));
        } catch (IOException ignored) {
            return false;
        }
    }

    public static File resolveBundledJar(String category, String moduleName) throws IOException {
        Path jar = bundledRoot().resolve(category).resolve(moduleName + ".jar");
        if (!Files.isRegularFile(jar)) {
            throw new IOException("Missing bundled extension jar: " + jar);
        }
        return jar.toFile();
    }
}
