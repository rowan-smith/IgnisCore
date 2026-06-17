package dev.rono.igniscore.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reports the Ignis API version compiled into this artifact and validates extension compatibility.
 *
 * <p>Values are loaded once from {@code ignis-api-version.properties} on the classpath. Extensions
 * may target an older API on the same major line; they may not require a newer API than the runtime
 * provides.</p>
 *
 * @see SemVersion
 */
public final class IgnisApiVersion {
    /** Raw version string from {@code ignis-api-version.properties}, for example {@code 1.2.0}. */
    public static final String CURRENT;
    /** Parsed {@link SemVersion} of {@link #CURRENT}. */
    public static final SemVersion CURRENT_SEMVER;

    static {
        try (InputStream input = IgnisApiVersion.class.getResourceAsStream("/ignis-api-version.properties")) {
            if (input == null) {
                throw new IllegalStateException("Missing ignis-api-version.properties");
            }
            Properties properties = new Properties();
            properties.load(input);
            String version = properties.getProperty("version");
            if (version == null || version.isBlank()) {
                throw new IllegalStateException("ignis-api-version.properties is missing version");
            }
            CURRENT = version;
            CURRENT_SEMVER = SemVersion.parse(version);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private IgnisApiVersion() {
    }

    /**
     * Ensures an extension's declared {@code api-version} is supported by this runtime.
     *
     * <p>Extensions may target an older API on the same major line; they may not require a newer API.</p>
     *
     * @param extensionApiVersion semver string from the extension manifest
     * @param extensionId extension identifier used in error messages
     * @throws IllegalStateException when the runtime API is older than the extension requires
     */
    public static void requireCompatible(String extensionApiVersion, String extensionId) {
        SemVersion required = SemVersion.parse(extensionApiVersion);
        if (SemVersion.isRuntimeCompatibleWith(CURRENT_SEMVER, required)) {
            return;
        }
        throw new IllegalStateException("Extension '" + extensionId + "' requires Ignis API "
                + required + " but runtime provides " + CURRENT_SEMVER
                + " (same major and runtime >= required)");
    }
}
