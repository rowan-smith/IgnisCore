package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

import java.util.Collection;

/**
 * High-level visual and audio effects for strategies and extensions.
 *
 * <p>Abstracts platform-specific sound playback, client-side explosion visuals,
 * and block preview rendering so strategies do not call world or protocol APIs
 * directly.</p>
 */
public interface IgnisEffectService {

    /**
     * Plays a named sound at a location for nearby players.
     *
     * @param location sound origin
     * @param soundName sound identifier (adapter resolves to a namespaced key)
     * @param volume volume multiplier
     * @param pitch pitch multiplier
     */
    void playSound(IgnisLocation location, String soundName, float volume, float pitch);

    /**
     * Shows an explosion effect to the given players without block damage.
     *
     * <p>May delegate to {@link IgnisProtocolService} when protocol integration
     * is enabled, or fall back to vanilla world effects.</p>
     *
     * @param location explosion center
     * @param power visual strength (similar to vanilla yield)
     * @param players audience that should see the effect
     */
    void playFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players);

    /**
     * Shows a temporary block preview at a location for one player.
     *
     * <p>Typically implemented with client-side block change packets so only
     * the target player sees the preview.</p>
     *
     * @param player viewer
     * @param location preview position
     * @param materialKey material to display
     */
    void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey);
}
