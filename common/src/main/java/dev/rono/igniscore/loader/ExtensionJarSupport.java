package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.YamlDefinitions;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class ExtensionJarSupport {
    private ExtensionJarSupport() {
    }

    record JarMetadata<T>(T manifest, Map<String, Object> config) {
    }

    static URLClassLoader createClassLoader(File jarFile, ClassLoader parent) throws Exception {
        return new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, parent);
    }

    static <T> JarMetadata<T> readMetadata(File jarFile,
                                           String manifestEntryName,
                                           Function<InputStream, T> manifestParser) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry manifestEntry = jar.getJarEntry(manifestEntryName);
            if (manifestEntry == null) {
                throw new IllegalStateException("Missing " + manifestEntryName + " in " + jarFile.getName());
            }

            T manifest;
            try (InputStream manifestStream = jar.getInputStream(manifestEntry)) {
                manifest = manifestParser.apply(manifestStream);
            }

            JarEntry configEntry = jar.getJarEntry("config.yml");
            if (configEntry == null) {
                throw new IllegalStateException("Missing config.yml in " + jarFile.getName());
            }

            Map<String, Object> config;
            try (InputStream configStream = jar.getInputStream(configEntry)) {
                config = YamlDefinitions.loadMap(configStream);
            }

            return new JarMetadata<>(manifest, config);
        }
    }

    static JarMetadata<ExtensionManifest> readExtensionMetadata(File jarFile, String manifestEntryName) throws Exception {
        String jarFallbackId = extensionIdFromJarName(jarFile.getName());
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry manifestEntry = jar.getJarEntry(manifestEntryName);
            if (manifestEntry == null) {
                throw new IllegalStateException("Missing " + manifestEntryName + " in " + jarFile.getName());
            }

            Map<String, Object> manifestConfig;
            try (InputStream manifestStream = jar.getInputStream(manifestEntry)) {
                manifestConfig = YamlDefinitions.loadMap(manifestStream);
            }

            JarEntry configEntry = jar.getJarEntry("config.yml");
            if (configEntry == null) {
                throw new IllegalStateException("Missing config.yml in " + jarFile.getName());
            }

            Map<String, Object> config;
            try (InputStream configStream = jar.getInputStream(configEntry)) {
                config = YamlDefinitions.loadMap(configStream);
            }

            ExtensionManifest manifest = ExtensionManifest.fromJarContents(
                    manifestConfig, config, manifestEntryName, jarFallbackId);
            return new JarMetadata<>(manifest, config);
        }
    }

    private static String extensionIdFromJarName(String jarFileName) {
        if (jarFileName == null || jarFileName.isBlank()) {
            return null;
        }
        return jarFileName.replaceFirst("(?i)\\.jar$", "");
    }

    static <T> T readManifest(File jarFile, String entryName, Function<InputStream, T> parser) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry(entryName);
            if (entry == null) {
                throw new IllegalStateException("Missing " + entryName + " in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return parser.apply(inputStream);
            }
        }
    }

    static Map<String, Object> readConfig(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("config.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing config.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return YamlDefinitions.loadMap(inputStream);
            }
        }
    }

    static IgnisStrategy loadStrategy(URLClassLoader classLoader,
                                      String strategyClassName,
                                      IgnisStrategyContext strategyContext,
                                      IgnisStrategyRegistry strategyRegistry,
                                      IgnisStrategyDescriptor descriptor,
                                      ExtensionKind kind) throws Exception {
        Class<?> strategyClass = Class.forName(strategyClassName, true, classLoader);
        Object instance = strategyClass.getConstructor(IgnisStrategyContext.class).newInstance(strategyContext);
        if (!(instance instanceof IgnisStrategy strategy)) {
            throw new IllegalStateException(strategyClassName + " does not implement IgnisStrategy");
        }

        if (!kind.strategyType().isInstance(strategy)) {
            throw new IllegalStateException(strategyClassName + " must implement " + kind.strategyType().getSimpleName());
        }

        if (strategy instanceof AbstractIgnisStrategy abstractStrategy) {
            abstractStrategy.bindDescriptor(descriptor);
        }

        strategyRegistry.register(descriptor, strategy);
        return strategy;
    }
}
