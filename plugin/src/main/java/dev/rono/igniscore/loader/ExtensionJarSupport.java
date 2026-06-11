package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

final class ExtensionJarSupport {
    private ExtensionJarSupport() {
    }

    static URLClassLoader createClassLoader(File jarFile, ClassLoader parent) throws Exception {
        return new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, parent);
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

    static YamlConfiguration readConfig(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            JarEntry entry = jar.getJarEntry("config.yml");
            if (entry == null) {
                throw new IllegalStateException("Missing config.yml in " + jarFile.getName());
            }

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

        Class<? extends IgnisStrategy> expectedType = kind == ExtensionKind.BLOCK
                ? IgnisBlockStrategy.class
                : IgnisItemStrategy.class;
        if (!expectedType.isInstance(strategy)) {
            throw new IllegalStateException(strategyClassName + " must implement " + expectedType.getSimpleName());
        }

        if (strategy instanceof AbstractIgnisStrategy abstractStrategy) {
            abstractStrategy.bindDescriptor(descriptor);
        }

        strategyRegistry.register(descriptor, strategy);
        return strategy;
    }
}
