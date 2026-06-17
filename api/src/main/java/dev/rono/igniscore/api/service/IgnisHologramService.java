package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.List;

/**
 * Text hologram integration. Bukkit may use DecentHolograms when present, otherwise
 * vanilla display entities. Sponge implementations may use platform-specific plugins.
 */
public interface IgnisHologramService extends IgnisIntegration {

    @Override
    default String integrationId() {
        return IgnisIntegrations.HOLOGRAM;
    }

    /**
     * @return opaque platform hologram handle, or {@code null} if creation failed
     */
    Object createTextHologram(IgnisLocation location, List<String> lines);

    void updateText(Object hologramHandle, List<String> lines);

    void delete(Object hologramHandle);

    void teleport(Object hologramHandle, IgnisLocation location);
}
