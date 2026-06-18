package dev.rono.igniscore.common.runtime;

import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Platform-neutral host surface for extension loading and shared runtime services.
 */
public interface IgnisRuntimeHost {

    Logger getLogger();

    Path getDataDirectory();

    ClassLoader getExtensionParentClassLoader();

    void debug(String message);
}
