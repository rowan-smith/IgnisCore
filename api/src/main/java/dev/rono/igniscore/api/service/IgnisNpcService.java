package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

/**
 * NPC integration (e.g. Citizens on Bukkit). No platform fallback — extensions that
 * require {@link IgnisIntegrations#NPC} are skipped when this integration is disabled.
 */
public interface IgnisNpcService extends IgnisIntegration {

    @Override
    default String integrationId() {
        return IgnisIntegrations.NPC;
    }

    /**
     * @return opaque platform NPC handle, or {@code null} if creation failed
     */
    Object spawnNpc(IgnisLocation location, String displayName);

    void setTarget(Object npcHandle, IgnisPlayer target);

    void remove(Object npcHandle);
}
