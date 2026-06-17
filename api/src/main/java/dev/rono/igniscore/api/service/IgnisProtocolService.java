package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

import java.util.Collection;

/**
 * Low-level protocol integration for advanced client-side effects.
 */
public interface IgnisProtocolService {

    boolean isEnabled();

    void sendFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players);

    /**
     * Attach the viewer's camera to {@code platformEntity} for {@code durationTicks}.
     */
    default void attachEntityCamera(IgnisPlayer viewer, Object platformEntity, int durationTicks) {
    }
}
