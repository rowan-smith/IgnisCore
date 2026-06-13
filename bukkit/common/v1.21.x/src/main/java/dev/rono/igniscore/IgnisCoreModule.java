package dev.rono.igniscore;

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
import dev.rono.igniscore.command.CommandRegistrar;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;
import dev.rono.igniscore.core.IgnisCoreFacadeImpl;
import dev.rono.igniscore.core.IgnisRuntimeLifecycle;
import dev.rono.igniscore.listener.BlockListener;
import dev.rono.igniscore.listener.ExtensionSupportListener;
import dev.rono.igniscore.listener.ItemListener;
import dev.rono.igniscore.listener.PlacedBlockRestoreListener;
import dev.rono.igniscore.listener.ResourcePackStatusListener;
import dev.rono.igniscore.module.IgnisCommonModule;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.BlockItemIdentifier;
import dev.rono.igniscore.service.ConfiguredEffectService;
import dev.rono.igniscore.service.CustomBlockBreakService;
import dev.rono.igniscore.service.CustomBlockIgnitionService;
import dev.rono.igniscore.service.CustomBlockPlacementService;
import dev.rono.igniscore.service.ItemFactory;
import dev.rono.igniscore.service.ItemIdentifier;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.IgnisEffectServiceImpl;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.service.VisualEffectService;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import dev.rono.igniscore.spigot.platform.SpigotPlatformIntegration;
import dev.rono.igniscore.spigot.renderer.BukkitBlockVisualRenderer;
import dev.rono.igniscore.spigot.runtime.SpigotRuntimeHost;
import dev.rono.igniscore.spigot.service.BukkitCustomItemFactory;
import dev.rono.igniscore.spigot.service.BukkitResourcePackHost;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class IgnisCoreModule extends AbstractModule {
    private final JavaPlugin plugin;
    private final PlatformAdapter platformAdapter;
    private final PlatformHooks platformHooks;
    private final IgnisRuntimeHost runtimeHost;

    public IgnisCoreModule(JavaPlugin plugin, PlatformAdapter platformAdapter) {
        this.plugin = plugin;
        this.platformAdapter = platformAdapter;
        if (!(platformAdapter instanceof BukkitPlatformAdapter bukkitAdapter)) {
            throw new IllegalArgumentException("Bukkit-family hosts require BukkitPlatformAdapter");
        }
        this.platformHooks = bukkitAdapter.legacyHooks();
        this.runtimeHost = new SpigotRuntimeHost(plugin);
    }

    public IgnisCoreModule(Main plugin, PlatformHooks platformHooks) {
        this(plugin, new BukkitPlatformAdapter(plugin, platformHooks));
    }

    @Override
    protected void configure() {
        install(new IgnisCommonModule());

        bind(JavaPlugin.class).toInstance(plugin);
        bind(Plugin.class).toInstance(plugin);
        if (plugin instanceof Main main) {
            bind(Main.class).toInstance(main);
        }
        bind(PlatformAdapter.class).toInstance(platformAdapter);
        bind(PlatformHooks.class).toInstance(platformHooks);
        bind(IgnisRuntimeHost.class).toInstance(runtimeHost);

        bind(BlockVisualRenderer.class).to(BukkitBlockVisualRenderer.class).in(Scopes.SINGLETON);
        bind(IgnisCustomItemFactory.class).to(BukkitCustomItemFactory.class).in(Scopes.SINGLETON);
        bind(ResourcePackHost.class).to(BukkitResourcePackHost.class).in(Scopes.SINGLETON);
        bind(IgnisPlatformIntegration.class).to(SpigotPlatformIntegration.class).in(Scopes.SINGLETON);

        bind(CommandRegistrar.class).in(Scopes.SINGLETON);
        bind(IgnisCommand.class).in(Scopes.SINGLETON);
        bind(BlockListener.class).in(Scopes.SINGLETON);
        bind(ItemListener.class).in(Scopes.SINGLETON);
        bind(ExtensionSupportListener.class).in(Scopes.SINGLETON);
        bind(ResourcePackStatusListener.class).in(Scopes.SINGLETON);
        bind(PlacedBlockRestoreListener.class).in(Scopes.SINGLETON);

        bind(NBTService.class).in(Scopes.SINGLETON);
        bind(IgnisNbtService.class).to(NBTService.class).in(Scopes.SINGLETON);
        bind(ProtocolService.class).in(Scopes.SINGLETON);
        bind(IgnisProtocolService.class).to(ProtocolService.class).in(Scopes.SINGLETON);
        bind(IgnisEffectService.class).to(IgnisEffectServiceImpl.class).in(Scopes.SINGLETON);
        bind(VisualEffectService.class).in(Scopes.SINGLETON);
        bind(ResourcePackService.class).in(Scopes.SINGLETON);
        bind(BlockItemFactory.class).in(Scopes.SINGLETON);
        bind(ItemFactory.class).in(Scopes.SINGLETON);
        bind(BlockItemIdentifier.class).in(Scopes.SINGLETON);
        bind(ItemIdentifier.class).in(Scopes.SINGLETON);
        bind(ConfiguredEffectService.class).in(Scopes.SINGLETON);
        bind(CustomBlockPlacementService.class).in(Scopes.SINGLETON);
        bind(CustomBlockBreakService.class).in(Scopes.SINGLETON);
        bind(CustomBlockIgnitionService.class).in(Scopes.SINGLETON);
    }
}
