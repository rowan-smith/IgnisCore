package dev.rono.igniscore.api.integration;

import java.util.Set;

/**
 * Runtime view of which optional integrations are available on this server.
 */
public interface IgnisIntegrationRegistry {

    boolean isEnabled(String integrationId);

    String providerName(String integrationId);

    Set<String> enabledIntegrationIds();

    void requireEnabled(String integrationId);
}
