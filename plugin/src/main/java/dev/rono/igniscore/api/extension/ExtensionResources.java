package dev.rono.igniscore.api.extension;

import java.io.InputStream;
import java.net.URLClassLoader;

public final class ExtensionResources {
    private final URLClassLoader classLoader;

    public ExtensionResources(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public InputStream open(String path) {
        return classLoader.getResourceAsStream(path);
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}
