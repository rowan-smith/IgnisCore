package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class PocketDimensionCacheListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    PocketDimensionCacheListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "pocket-dimension-cache");
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Pocket Cache") : definition.getTitle();
    }

    private int rows(BlockDefinition definition) {
        return Math.min(6, Math.max(1, StrategySupport.customInt(definition, "storageRows", 3)));
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.registerPerPlayer(event.block().location(), title(event.block().definition()), rows(event.block().definition()));
                IgnisLocation center = Locations.toCenter(event.block().location());
                TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                registry.unregister(event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() != CustomBlockAction.OPEN) {
                    return;
                }
                registry.openPerPlayer(event.player(), event.block().location(), title(event.block().definition()), rows(event.block().definition()));
                IgnisWorld world = worldAt(event.block().location());
                IgnisLocation center = Locations.toCenter(event.block().location());
                TheatricsSupport.sparkle(world, center, "PORTAL", 10);
                world.playSound(center, "BLOCK_ENDER_CHEST_OPEN", 0.7f, 1.1f);
                event.player().sendMessage("<light_purple>Opened your pocket dimension cache.</light_purple>");
    }
}
