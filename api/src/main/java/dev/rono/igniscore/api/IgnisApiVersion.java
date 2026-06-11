package dev.rono.igniscore.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class IgnisApiVersion {
    public static final String CURRENT;

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
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private IgnisApiVersion() {
    }

    public static void requireCompatible(String extensionApiVersion, String extensionId) {
        if (CURRENT.equals(extensionApiVersion)) {
            return;
        }
        throw new IllegalStateException("Extension '" + extensionId + "' requires Ignis API "
                + extensionApiVersion + " but runtime provides " + CURRENT);
    }
}
