package dev.rono.igniscore.block.picnicbasket;

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
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;

final class PicnicBasketListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private static final int STORAGE_SLOTS = 6;
    private static final Map<String, Long> LAST_OPEN = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    PicnicBasketListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "picnic-basket");
    }

    private static String blockKey(IgnisLocation location) {
        IgnisLocation b = Locations.toBlock(location);
        return b.worldName() + ":" + (int) b.x() + ":" + (int) b.y() + ":" + (int) b.z();
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Picnic Basket") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.registerBlock(event.block().location(), title(event.block().definition()), 1);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                registry.unregister(event.block().location());
                LAST_OPEN.remove(blockKey(event.block().location()));
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() != CustomBlockAction.OPEN) {
                    return;
                }
                String key = blockKey(event.block().location());
                long now = System.currentTimeMillis();
                Long previous = LAST_OPEN.put(key, now);
                registry.openBlock(event.player(), event.block().location());
                if (previous != null && now - previous < 5000L) {
                    IgnisWorld world = worldAt(event.block().location());
                    IgnisLocation center = Locations.toCenter(event.block().location());
                    for (IgnisPlayer nearby : world.getPlayersNear(center, 6.0)) {
                        nearby.applyPotionEffect("SATURATION", 100, 0);
                        nearby.sendMessage("<gold>Shared picnic — saturation boost!</gold>");
                    }
                    TheatricsSupport.sparkle(world, center, "HEART", 10);
                }
    }
}
