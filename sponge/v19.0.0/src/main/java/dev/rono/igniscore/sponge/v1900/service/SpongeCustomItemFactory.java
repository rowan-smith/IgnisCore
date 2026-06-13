package dev.rono.igniscore.sponge.v1900.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisCustomItemFactory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.sponge.v1900.adapter.SpongeBridge;

public class SpongeCustomItemFactory implements IgnisCustomItemFactory {
    private final SpongeItemFactory itemFactory;

    @Inject
    public SpongeCustomItemFactory(SpongeItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    @Override
    public IgnisItem createBlockItem(String typeId) {
        return SpongeBridge.wrap(itemFactory.createItem(typeId));
    }

    @Override
    public IgnisItem createItem(String typeId) {
        return SpongeBridge.wrap(itemFactory.createItem(typeId));
    }
}
