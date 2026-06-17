package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class QuarryCacheListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private final QuarryCacheRegistry registry;

    QuarryCacheListeners(IgnisStrategyContext context) {
        this.registry = new QuarryCacheRegistry(context);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        registry.register(event.block().location(), event.definition(), event.placedFrom());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        registry.handleBreak(event.block().location(), event.droppedItem());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            registry.openGui(event.player(), event.block().location());
        }
    }
}
