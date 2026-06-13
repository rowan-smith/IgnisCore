package dev.rono.igniscore.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class IgnisApiVersion {
    public static final String CURRENT;
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
     * Extensions may target an older API on the same major line; they may not require a newer API.
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
