package dev.rono.igniscore.sponge.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisProtocolService;

import java.util.Collection;

public final class SpongeNoopProtocolService implements IgnisProtocolService {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void sendFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
    }
}
