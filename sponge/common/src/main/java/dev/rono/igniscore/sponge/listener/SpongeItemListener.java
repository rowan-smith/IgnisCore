package dev.rono.igniscore.sponge.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.service.SpongeItemIdentifier;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeItemListener {
    private final ItemManager itemManager;
    private final SpongeItemIdentifier itemIdentifier;
    private final StrategyEventPublisher events;

    @Inject
    public SpongeItemListener(ItemManager itemManager,
                              SpongeItemIdentifier itemIdentifier,
                              StrategyEventPublisher events) {
        this.itemManager = itemManager;
        this.itemIdentifier = itemIdentifier;
        this.events = events;
    }

    @Listener(order = Order.LATE)
    public void onInteractItem(InteractItemEvent event, @First ServerPlayer player) {
        ItemStack itemStack = event.itemStack().asImmutable().asMutable();
        String typeId = itemIdentifier.resolveTypeId(itemStack);
        if (typeId == null) {
            return;
        }

        ItemDefinition definition = itemManager.getItemTypes().get(typeId);
        if (definition == null) {
            return;
        }

        if (event instanceof org.spongepowered.api.event.Cancellable cancellable) {
            cancellable.setCancelled(true);
        }

        events.fireItemClick(
                SpongeBridge.wrap(player),
                definition,
                SpongeBridge.wrap(itemStack),
                SpongeBridge.toIgnisInteraction(event),
                null);
    }
}
