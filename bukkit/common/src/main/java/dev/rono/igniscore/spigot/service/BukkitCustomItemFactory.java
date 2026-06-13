package dev.rono.igniscore.spigot.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisCustomItemFactory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.service.BlockItemFactory;
import dev.rono.igniscore.service.ItemFactory;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;

public class BukkitCustomItemFactory implements IgnisCustomItemFactory {
    private final BlockItemFactory blockItemFactory;
    private final ItemFactory itemFactory;

    @Inject
    public BukkitCustomItemFactory(BlockItemFactory blockItemFactory, ItemFactory itemFactory) {
        this.blockItemFactory = blockItemFactory;
        this.itemFactory = itemFactory;
    }

    @Override
    public IgnisItem createBlockItem(String typeId) {
        return BukkitBridge.wrap(blockItemFactory.createBlockItem(typeId));
    }

    @Override
    public IgnisItem createItem(String typeId) {
        return BukkitBridge.wrap(itemFactory.createItem(typeId));
    }
}
