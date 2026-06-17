package dev.rono.igniscore.integration;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.integration.IntegrationUnavailableException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default registry backed by registered {@link IgnisIntegration} instances.
 */
public final class DefaultIgnisIntegrationRegistry implements IgnisIntegrationRegistry {
    private final Map<String, IgnisIntegration> integrations = new HashMap<>();

    public DefaultIgnisIntegrationRegistry register(IgnisIntegration integration) {
        integrations.put(integration.integrationId(), integration);
        return this;
    }

    public DefaultIgnisIntegrationRegistry registerCapability(String integrationId, IgnisIntegration integration) {
        integrations.put(integrationId, integration);
        return this;
    }

    @Override
    public boolean isEnabled(String integrationId) {
        IgnisIntegration integration = integrations.get(integrationId);
        return integration != null && integration.isEnabled();
    }

    @Override
    public String providerName(String integrationId) {
        IgnisIntegration integration = integrations.get(integrationId);
        return integration != null ? integration.providerName() : "unavailable";
    }

    @Override
    public Set<String> enabledIntegrationIds() {
        Set<String> enabled = new HashSet<>();
        for (Map.Entry<String, IgnisIntegration> entry : integrations.entrySet()) {
            if (entry.getValue().isEnabled()) {
                enabled.add(entry.getKey());
            }
        }
        return Set.copyOf(enabled);
    }

    @Override
    public void requireEnabled(String integrationId) {
        if (!isEnabled(integrationId)) {
            throw new IntegrationUnavailableException(integrationId, providerName(integrationId));
        }
    }
}
