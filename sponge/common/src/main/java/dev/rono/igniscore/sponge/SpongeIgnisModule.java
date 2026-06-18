package dev.rono.igniscore.sponge;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;
import dev.rono.igniscore.api.port.BlockVisualRenderer;
import dev.rono.igniscore.api.port.IgnisCustomItemFactory;
import dev.rono.igniscore.api.port.IgnisPlatformIntegration;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.ResourcePackHost;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.module.IgnisCommonModule;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.command.SpongeIgnisCommand;
import dev.rono.igniscore.sponge.config.SpongeIgnisConfig;
import dev.rono.igniscore.sponge.integration.hologram.SpongeHologramService;
import dev.rono.igniscore.sponge.integration.region.SpongeCompositeRegionService;
import dev.rono.igniscore.sponge.integration.region.SpongeWorldEditRegionService;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeExtensionSupportListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.listener.SpongePlacedBlockRestoreListener;
import dev.rono.igniscore.sponge.platform.SpongePlatformIntegration;
import dev.rono.igniscore.sponge.renderer.SpongeBlockVisualRenderer;
import dev.rono.igniscore.sponge.resourcepack.SpongeResourcePackService;
import dev.rono.igniscore.sponge.runtime.SpongeRuntimeHost;
import dev.rono.igniscore.sponge.service.SpongeBlockItemFactory;
import dev.rono.igniscore.sponge.service.SpongeBlockItemIdentifier;
import dev.rono.igniscore.sponge.service.SpongeConfiguredEffectService;
import dev.rono.igniscore.sponge.service.SpongeCustomBlockBreakService;
import dev.rono.igniscore.sponge.service.SpongeCustomBlockIgnitionService;
import dev.rono.igniscore.sponge.service.SpongeCustomBlockPlacementService;
import dev.rono.igniscore.sponge.service.SpongeCustomItemFactory;
import dev.rono.igniscore.sponge.service.SpongeEffectService;
import dev.rono.igniscore.sponge.service.SpongeExtensionReloadService;
import dev.rono.igniscore.sponge.service.SpongeItemFactory;
import dev.rono.igniscore.sponge.service.SpongeItemIdentifier;
import dev.rono.igniscore.sponge.service.SpongeNbtService;
import dev.rono.igniscore.sponge.service.SpongeNoopNpcService;
import dev.rono.igniscore.sponge.service.SpongeProtocolService;
import dev.rono.igniscore.sponge.service.SpongeResourcePackHost;

public class SpongeIgnisModule extends AbstractModule {
    private final SpongePluginHost plugin;
    private final SpongePlatformAdapter platformAdapter;
    private final SpongePluginContext pluginContext;
    private final SpongeIgnisConfig config;

    public SpongeIgnisModule(SpongePluginHost plugin, PlatformAdapter platformAdapter) {
        this.plugin = plugin;
        if (!(platformAdapter instanceof SpongePlatformAdapter spongeAdapter)) {
            throw new IllegalArgumentException("Sponge hosts require SpongePlatformAdapter");
        }
        this.platformAdapter = spongeAdapter;
        this.config = new SpongeIgnisConfig(platformAdapter);
        this.pluginContext = new SpongePluginContext(plugin);
    }

    @Override
    protected void configure() {
        install(Modules.override(new IgnisCommonModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                bind(PerformanceSettings.class).toInstance(config.performanceSettings());
            }
        }));

        bind(SpongePluginHost.class).toInstance(plugin);
        bind(SpongePluginContext.class).toInstance(pluginContext);
        bind(SpongeIgnisConfig.class).toInstance(config);
        bind(SpongePlatformAdapter.class).toInstance(platformAdapter);
        bind(PlatformAdapter.class).toInstance(platformAdapter);
        bind(IgnisRuntimeHost.class).toInstance(new SpongeRuntimeHost(
                plugin,
                platformAdapter.container(),
                platformAdapter.getDataDirectory(),
                platformAdapter.getLogger(),
                pluginContext));

        bind(BlockVisualRenderer.class).to(SpongeBlockVisualRenderer.class).in(Scopes.SINGLETON);
        bind(IgnisCustomItemFactory.class).to(SpongeCustomItemFactory.class).in(Scopes.SINGLETON);
        bind(ResourcePackHost.class).to(SpongeResourcePackHost.class).in(Scopes.SINGLETON);
        bind(IgnisPlatformIntegration.class).to(SpongePlatformIntegration.class).in(Scopes.SINGLETON);

        bind(SpongeIgnisCommand.class).in(Scopes.SINGLETON);
        bind(SpongeItemListener.class).in(Scopes.SINGLETON);
        bind(SpongeBlockListener.class).in(Scopes.SINGLETON);
        bind(SpongeExtensionSupportListener.class).in(Scopes.SINGLETON);
        bind(SpongePlacedBlockRestoreListener.class).in(Scopes.SINGLETON);

        bind(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(IgnisNbtService.class).to(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(SpongeWorldEditRegionService.class).in(Scopes.SINGLETON);
        bind(SpongeCompositeRegionService.class).in(Scopes.SINGLETON);
        bind(IgnisRegionService.class).to(SpongeCompositeRegionService.class).in(Scopes.SINGLETON);
        bind(SpongeHologramService.class).in(Scopes.SINGLETON);
        bind(IgnisHologramService.class).to(SpongeHologramService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopNpcService.class).in(Scopes.SINGLETON);
        bind(IgnisNpcService.class).to(SpongeNoopNpcService.class).in(Scopes.SINGLETON);
        bind(SpongeProtocolService.class).in(Scopes.SINGLETON);
        bind(IgnisProtocolService.class).to(SpongeProtocolService.class).in(Scopes.SINGLETON);
        bind(SpongeEffectService.class).in(Scopes.SINGLETON);
        bind(IgnisEffectService.class).to(SpongeEffectService.class).in(Scopes.SINGLETON);

        bind(SpongeItemFactory.class).in(Scopes.SINGLETON);
        bind(SpongeItemIdentifier.class).in(Scopes.SINGLETON);
        bind(SpongeBlockItemFactory.class).in(Scopes.SINGLETON);
        bind(SpongeBlockItemIdentifier.class).in(Scopes.SINGLETON);
        bind(SpongeConfiguredEffectService.class).in(Scopes.SINGLETON);
        bind(SpongeCustomBlockPlacementService.class).in(Scopes.SINGLETON);
        bind(SpongeCustomBlockBreakService.class).in(Scopes.SINGLETON);
        bind(SpongeCustomBlockIgnitionService.class).in(Scopes.SINGLETON);
        bind(SpongeExtensionReloadService.class).in(Scopes.SINGLETON);
        bind(SpongeResourcePackService.class).in(Scopes.SINGLETON);

        bind(SpongeIgnisApplication.class).in(Scopes.SINGLETON);
    }
}
