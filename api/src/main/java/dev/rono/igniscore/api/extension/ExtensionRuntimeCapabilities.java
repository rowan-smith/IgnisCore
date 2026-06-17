package dev.rono.igniscore.api.extension;

/**
 * Reports which optional integrations are available on the running server.
 *
 * @param protocolEnabled whether {@link ExtensionIntegration#PROTOCOL} is active
 * @param nbtEntityEnabled whether {@link ExtensionIntegration#NBT_ENTITY} is active
 */
public record ExtensionRuntimeCapabilities(boolean protocolEnabled, boolean nbtEntityEnabled) {

    /**
     * @return capabilities with every integration enabled (unit tests)
     */
    public static ExtensionRuntimeCapabilities allEnabled() {
        return new ExtensionRuntimeCapabilities(true, true);
    }

    /**
     * @return whether the given integration is available
     */
    public boolean isEnabled(ExtensionIntegration integration) {
        return switch (integration) {
            case PROTOCOL -> protocolEnabled;
            case NBT_ENTITY -> nbtEntityEnabled;
        };
    }
}
