package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.sponge.SpongePluginHost;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeNativePacketSupport;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Collection;

/**
 * Sponge protocol integration using native vanilla packets when available,
 * with explosion particle fallback through the Viewer API.
 */
public final class SpongeProtocolService implements IgnisProtocolService {
    private final SpongeNativePacketSupport nativePackets;

    @Inject
    public SpongeProtocolService(SpongePluginHost plugin) {
        this.nativePackets = new SpongeNativePacketSupport(plugin.getLogger());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String providerName() {
        return nativePackets.isAvailable() ? "native-packet" : "particles";
    }

    @Override
    public void sendFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
        if (players.isEmpty()) {
            return;
        }
        ParticleEffect effect = ParticleEffect.builder()
                .type(ParticleTypes.EXPLOSION_EMITTER)
                .quantity(1)
                .build();

        for (IgnisPlayer player : players) {
            ServerPlayer spongePlayer = SpongeBridge.unwrap(player);
            if (nativePackets.isAvailable()) {
                nativePackets.sendFakeExplosion(spongePlayer, location.x(), location.y(), location.z(), power);
            }
            spongePlayer.spawnParticles(effect, location.x(), location.y(), location.z());
        }
    }
}
