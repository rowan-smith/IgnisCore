package dev.rono.igniscore.sponge.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisHologramService;

import java.util.List;

public final class SpongeNoopHologramService implements IgnisHologramService {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String providerName() {
        return "unavailable";
    }

    @Override
    public Object createTextHologram(IgnisLocation location, List<String> lines) {
        return null;
    }

    @Override
    public void updateText(Object hologramHandle, List<String> lines) {
    }

    @Override
    public void delete(Object hologramHandle) {
    }

    @Override
    public void teleport(Object hologramHandle, IgnisLocation location) {
    }
}
