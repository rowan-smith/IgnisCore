package dev.rono.igniscore.api.integration;

/**
 * Thrown when an extension or feature requires an integration that is not available.
 */
public final class IntegrationUnavailableException extends RuntimeException {
    private final String integrationId;

    public IntegrationUnavailableException(String integrationId, String providerName) {
        super("Required integration '" + integrationId + "' is not available (provider: " + providerName + ")");
        this.integrationId = integrationId;
    }

    public String getIntegrationId() {
        return integrationId;
    }
}
