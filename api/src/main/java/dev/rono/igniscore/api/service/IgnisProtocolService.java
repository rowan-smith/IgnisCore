package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

import java.util.Collection;

/**
 * Low-level protocol integration for advanced client-side effects.
 *
 * <p>Sends packets that vanilla APIs do not expose, such as fake explosions
 * visible only to selected players. When {@link #isEnabled()} is false,
 * callers should use higher-level services or vanilla fallbacks.</p>
 */
public interface IgnisProtocolService {

    /**
     * @return whether protocol packet injection is available on this platform
     */
    boolean isEnabled();

    /**
     * Sends a client-side explosion packet to the given players.
     *
     * <p>Does not break blocks or deal damage; purely visual and auditory
     * on the client.</p>
     *
     * @param location explosion center
     * @param power explosion strength sent to clients
     * @param players recipients of the packet
     */
    void sendFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players);

    /**
     * Attach the viewer's camera to {@code platformEntity} for {@code durationTicks}.
     */
    default void attachEntityCamera(IgnisPlayer viewer, Object platformEntity, int durationTicks) {
    }
}
