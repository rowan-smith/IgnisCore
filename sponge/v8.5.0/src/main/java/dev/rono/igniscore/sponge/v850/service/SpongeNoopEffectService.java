package dev.rono.igniscore.sponge.v850.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.sponge.v850.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.v850.support.SpongeRuntimeHolder;

import java.util.Collection;

public final class SpongeNoopEffectService implements IgnisEffectService {
    private final PlatformAdapter platformAdapter;

    public SpongeNoopEffectService(PlatformAdapter platformAdapter) {
        this.platformAdapter = platformAdapter;
    }

    @Override
    public void playSound(IgnisLocation location, String soundName, float volume, float pitch) {
        var world = SpongeRuntimeHolder.server().worldManager().worlds().stream().findFirst().orElse(null);
        if (world != null) {
            SpongeBridge.wrap(world).playSound(location, soundName, volume, pitch);
        }
    }

    @Override
    public void playFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
    }

    @Override
    public void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey) {
    }
}
