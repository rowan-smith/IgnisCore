package dev.rono.igniscore.sponge.platform;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisPlatformIntegration;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import dev.rono.igniscore.sponge.listener.SpongeBlockListener;
import dev.rono.igniscore.sponge.listener.SpongeExtensionSupportListener;
import dev.rono.igniscore.sponge.listener.SpongeItemListener;
import dev.rono.igniscore.sponge.listener.SpongePlacedBlockRestoreListener;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.plugin.PluginContainer;

import java.util.List;

public class SpongePlatformIntegration implements IgnisPlatformIntegration {
    private final PluginContainer container;
    private final EventManager eventManager;
    private final PlatformAdapter platformAdapter;
    private final SpongePlacedBlockRestoreListener placedBlockRestoreListener;
    private final List<Object> listeners;

    @Inject
    public SpongePlatformIntegration(SpongePlatformAdapter platformAdapter,
                                     SpongeItemListener itemListener,
                                     SpongeBlockListener blockListener,
                                     SpongeExtensionSupportListener extensionSupportListener,
                                     SpongePlacedBlockRestoreListener placedBlockRestoreListener) {
        this.container = platformAdapter.container();
        this.eventManager = platformAdapter.eventManager();
        this.platformAdapter = platformAdapter;
        this.placedBlockRestoreListener = placedBlockRestoreListener;
        this.listeners = List.of(itemListener, blockListener, extensionSupportListener, placedBlockRestoreListener);
    }

    @Override
    public void onRuntimeEnable() {
        for (Object listener : listeners) {
            eventManager.registerListeners(container, listener);
        }
        platformAdapter.getScheduler().runGlobal(placedBlockRestoreListener::restoreLoadedChunks);
    }

    @Override
    public void onRuntimeDisable() {
    }
}
