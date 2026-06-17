package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

import java.util.Collection;

/**
 * Low-level protocol integration for advanced client-side effects.
 */
public interface IgnisProtocolService extends IgnisIntegration {

    @Override
    default String integrationId() {
        return IgnisIntegrations.PROTOCOL;
    }

    void sendFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players);
}
