package dev.rono.igniscore.sponge;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.manager.BlockTypeRegistry;
import dev.rono.igniscore.module.IgnisCommonModule;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.command.SpongeIgnisCommand;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.runtime.SpongeRuntimeHost;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;
import dev.rono.igniscore.sponge.service.SpongeExtensionSupportService;
import dev.rono.igniscore.sponge.service.SpongeItemFactory;
import dev.rono.igniscore.sponge.service.SpongeItemIdentifier;
import dev.rono.igniscore.sponge.service.SpongeNbtService;
import dev.rono.igniscore.sponge.service.SpongeNoopEffectService;
import dev.rono.igniscore.sponge.service.SpongeNoopProtocolService;

public class SpongeIgnisModule extends AbstractModule {
    private final IgnisSpongePlugin plugin;
    private final SpongePlatformAdapter platformAdapter;

    public SpongeIgnisModule(IgnisSpongePlugin plugin, SpongePlatformAdapter platformAdapter) {
        this.plugin = plugin;
        this.platformAdapter = platformAdapter;
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

        bind(SpongeIgnisCommand.class).in(Scopes.SINGLETON);
        bind(SpongeItemListener.class).in(Scopes.SINGLETON);
        bind(SpongeBlockListener.class).in(Scopes.SINGLETON);

        bind(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(IgnisNbtService.class).to(SpongeNbtService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopProtocolService.class).in(Scopes.SINGLETON);
        bind(IgnisProtocolService.class).to(SpongeNoopProtocolService.class).in(Scopes.SINGLETON);
        bind(SpongeNoopEffectService.class).in(Scopes.SINGLETON);
        bind(IgnisEffectService.class).to(SpongeNoopEffectService.class).in(Scopes.SINGLETON);

        bind(SpongeBlockManager.class).in(Scopes.SINGLETON);
        bind(BlockTypeRegistry.class).to(SpongeBlockManager.class).in(Scopes.SINGLETON);
        bind(SpongeItemFactory.class).in(Scopes.SINGLETON);
        bind(SpongeItemIdentifier.class).in(Scopes.SINGLETON);
        bind(SpongeExtensionSupportService.class).in(Scopes.SINGLETON);
        bind(ExtensionSupport.class).to(SpongeExtensionSupportService.class).in(Scopes.SINGLETON);

        bind(SpongeIgnisFacade.class).in(Scopes.SINGLETON);
        bind(SpongeIgnisApplication.class).in(Scopes.SINGLETON);
    }
}
