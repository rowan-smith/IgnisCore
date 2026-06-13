package dev.rono.igniscore.sponge.v850;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import dev.rono.igniscore.api.port.BlockVisualRenderer;
import dev.rono.igniscore.api.port.IgnisCustomItemFactory;
import dev.rono.igniscore.api.port.IgnisPlatformIntegration;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.ResourcePackHost;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.module.IgnisCommonModule;
import dev.rono.igniscore.sponge.v850.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.v850.command.SpongeIgnisCommand;
import dev.rono.igniscore.sponge.v850.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.v850.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.v850.platform.SpongePlatformIntegration;
import dev.rono.igniscore.sponge.v850.renderer.NoopBlockVisualRenderer;
import dev.rono.igniscore.sponge.v850.runtime.SpongeRuntimeHost;
import dev.rono.igniscore.sponge.v850.service.SpongeCustomItemFactory;
import dev.rono.igniscore.sponge.v850.service.SpongeItemFactory;
import dev.rono.igniscore.sponge.v850.service.SpongeItemIdentifier;
import dev.rono.igniscore.sponge.v850.service.SpongeNbtService;
import dev.rono.igniscore.sponge.v850.service.SpongeNoopEffectService;
import dev.rono.igniscore.sponge.v850.service.SpongeNoopProtocolService;
import dev.rono.igniscore.sponge.v850.service.SpongeResourcePackHost;

public class SpongeIgnisModule extends AbstractModule {
    private final IgnisSpongePlugin plugin;
    private final SpongePlatformAdapter platformAdapter;

    public SpongeIgnisModule(IgnisSpongePlugin plugin, PlatformAdapter platformAdapter) {
        this.plugin = plugin;
        if (!(platformAdapter instanceof SpongePlatformAdapter spongeAdapter)) {
            throw new IllegalArgumentException("Sponge hosts require SpongePlatformAdapter");
        }
        this.platformAdapter = spongeAdapter;
    }

    @Override
    protected void configure() {
        install(new IgnisCommonModule());

        bind(IgnisSpongePlugin.class).toInstance(plugin);
        bind(SpongePlatformAdapter.class).toInstance(platformAdapter);
        bind(PlatformAdapter.class).toInstance(platformAdapter);
        bind(IgnisRuntimeHost.class).toInstance(new SpongeRuntimeHost(
                plugin,
                platformAdapter.container(),
                platformAdapter.getDataDirectory(),
                platformAdapter.getLogger()));

        bind(BlockVisualRenderer.class).to(NoopBlockVisualRenderer.class).in(Scopes.SINGLETON);
        bind(IgnisCustomItemFactory.class).to(SpongeCustomItemFactory.class).in(Scopes.SINGLETON);
        bind(ResourcePackHost.class).to(SpongeResourcePackHost.class).in(Scopes.SINGLETON);
        bind(IgnisPlatformIntegration.class).to(SpongePlatformIntegration.class).in(Scopes.SINGLETON);

        bind(SpongeIgnisCommand.class).in(Scopes.SINGLETON);
        bind(SpongeItemListener.class).in(Scopes.SINGLETON);
        bind(SpongeBlockListener.class).in(Scopes.SINGLETON);

        bind(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(IgnisNbtService.class).to(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopProtocolService.class).in(Scopes.SINGLETON);
        bind(IgnisProtocolService.class).to(SpongeNoopProtocolService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopEffectService.class).in(Scopes.SINGLETON);
        bind(IgnisEffectService.class).to(SpongeNoopEffectService.class).in(Scopes.SINGLETON);

        bind(SpongeItemFactory.class).in(Scopes.SINGLETON);
        bind(SpongeItemIdentifier.class).in(Scopes.SINGLETON);

        bind(SpongeIgnisApplication.class).in(Scopes.SINGLETON);
    }
}
