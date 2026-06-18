package dev.rono.igniscore.sponge.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisNpcService;

public final class SpongeNoopNpcService implements IgnisNpcService {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String providerName() {
        return "unavailable";
    }

    @Override
    public Object spawnNpc(IgnisLocation location, String displayName) {
        return null;
    }

    @Override
    public void setTarget(Object npcHandle, IgnisPlayer target) {
    }

    @Override
    public void remove(Object npcHandle) {
    }
}
