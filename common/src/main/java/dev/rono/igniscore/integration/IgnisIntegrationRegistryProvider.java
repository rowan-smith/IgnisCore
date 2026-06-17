package dev.rono.igniscore.integration;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;

@Singleton
public final class IgnisIntegrationRegistryProvider implements Provider<IgnisIntegrationRegistry> {
    private final IgnisIntegrationRegistry registry;

    @Inject
    public IgnisIntegrationRegistryProvider(IgnisNbtService nbtService,
                                             IgnisProtocolService protocolService,
                                             IgnisRegionService regionService,
                                             IgnisHologramService hologramService,
                                             IgnisNpcService npcService) {
        this.registry = new DefaultIgnisIntegrationRegistry()
                .register(nbtService)
                .register(protocolService)
                .register(regionService)
                .register(hologramService)
                .register(npcService)
                .registerCapability(IgnisIntegrations.NBT_ENTITY, capability(
                        IgnisIntegrations.NBT_ENTITY,
                        nbtService.supportsEntityData(),
                        nbtService.providerName()))
                .registerCapability(IgnisIntegrations.REGION_WORLDEDIT, capability(
                        IgnisIntegrations.REGION_WORLDEDIT,
                        regionService.isWorldEditBacked(),
                        regionService.providerName()));
    }

    @Override
    public IgnisIntegrationRegistry get() {
        return registry;
    }

    private static IgnisIntegration capability(String id, boolean enabled, String provider) {
        return new IgnisIntegration() {
            @Override
            public String integrationId() {
                return id;
            }

            @Override
            public boolean isEnabled() {
                return enabled;
            }

            @Override
            public String providerName() {
                return provider;
            }
        };
    }
}
