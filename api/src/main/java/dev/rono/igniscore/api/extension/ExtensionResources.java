package dev.rono.igniscore.api.extension;

import java.io.InputStream;
import java.net.URLClassLoader;

/**
 * Classpath resource access for a loaded extension JAR.
 *
 * <p>Strategies receive an instance scoped to their extension class loader so assets such as
 * textures, models, and nested YAML can be opened without hard-coding JAR paths.</p>
 *
 * @see ExtensionManifest
 */
public final class ExtensionResources {
    private final URLClassLoader classLoader;

    /**
     * @param classLoader class loader that loaded the extension JAR
     */
    public ExtensionResources(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Opens a resource stream relative to the extension JAR root.
     *
     * @param path classpath path (for example {@code textures/icon.png})
     * @return input stream, or {@code null} when the resource is missing
     */
    public InputStream open(String path) {
        return classLoader.getResourceAsStream(path);
    }

    /**
     * @return the class loader that loaded this extension
     */
    public URLClassLoader getClassLoader() {
        return classLoader;
    }
}
