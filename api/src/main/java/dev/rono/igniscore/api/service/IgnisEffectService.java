package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

import java.util.Collection;

/**
 * High-level visual and audio effects for strategies and extensions.
 */
public interface IgnisEffectService {

    void playSound(IgnisLocation location, String soundName, float volume, float pitch);

    void playFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players);

    void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey);
}
