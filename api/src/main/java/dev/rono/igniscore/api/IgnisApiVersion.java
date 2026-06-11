package dev.rono.igniscore.api;

public final class IgnisApiVersion {
    public static final String CURRENT = "1.0.0";

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
