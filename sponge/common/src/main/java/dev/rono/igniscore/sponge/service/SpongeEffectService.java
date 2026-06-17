package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRegistrySupport;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.Collection;

public final class SpongeEffectService implements IgnisEffectService {
    private final IgnisProtocolService protocolService;

    @Inject
    public SpongeEffectService(IgnisProtocolService protocolService) {
        this.protocolService = protocolService;
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
        protocolService.sendFakeExplosion(location, power, players);
    }

    @Override
    public void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey) {
        if (player == null) {
            return;
        }
        var spongePlayer = SpongeBridge.unwrap(player);
        var world = spongePlayer.world();
        ServerLocation serverLocation = SpongeBridge.toSponge(location, world);
        var blockType = SpongeRegistrySupport.findBlockType(ResourceKey.resolve(materialKey.toLowerCase())).orElse(null);
        if (blockType == null) {
            return;
        }
        spongePlayer.sendBlockChange(
                serverLocation.blockPosition().x(),
                serverLocation.blockPosition().y(),
                serverLocation.blockPosition().z(),
                blockType.defaultState());
    }
}
