package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
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

final class ChunkLoaderLiteListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    ChunkLoaderLiteListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "chunk-loader-lite");
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        var gui = registry.blockGui(location);
        boolean fueled = false;
        if (gui != null) {
            IgnisItem fuel = gui.inventory().getItem(0);
            fueled = fuel != null && !fuel.isAir() && isFuel(fuel.getMaterialKey());
            if (fueled && StrategySupport.customBoolean(definition, "consumeFuel", true)) {
                fuel.setAmount(fuel.getAmount() - 1);
                gui.inventory().setItem(0, fuel.getAmount() > 0 ? fuel : null);
            }
        }
        world.setChunkForceLoaded(location, fueled);
        if (fueled) {
            TheatricsSupport.pulseRing(world, center, 2.5, "PORTAL");
            world.playSound(center, "BLOCK_BEACON_AMBIENT", 0.2f, 1.5f);
        }
    }

    private boolean isFuel(String material) {
        String key = material.toLowerCase();
        return key.contains("coal") || key.contains("charcoal") || key.contains("blaze_rod");
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Chunk Loader Fuel") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                IgnisWorld world = worldAt(event.block().location());
                world.setChunkForceLoaded(event.block().location(), true);
                registry.registerBlock(event.block().location(), title(event.block().definition()), 1);
                long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 40);
                PlacedTickSupport.start(context, event.block().location(), period, () -> tick(event.block().definition(), event.block().location()));
                TheatricsSupport.chime(world, Locations.toCenter(event.block().location()), 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
                IgnisWorld world = worldAt(event.block().location());
                world.setChunkForceLoaded(event.block().location(), false);
                registry.unregister(event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() == CustomBlockAction.OPEN) {
                    registry.openBlock(event.player(), event.block().location());
                }
    }
}
