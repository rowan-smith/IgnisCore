package dev.rono.igniscore.sponge.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.service.SpongeItemIdentifier;
import dev.rono.igniscore.manager.ItemManager;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeItemListener {
    private final ItemManager itemManager;
    private final SpongeItemIdentifier itemIdentifier;
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public SpongeItemListener(ItemManager itemManager,
                              SpongeItemIdentifier itemIdentifier,
                              IgnisStrategyRegistry strategyRegistry) {
        this.itemManager = itemManager;
        this.itemIdentifier = itemIdentifier;
        this.strategyRegistry = strategyRegistry;
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

        requireItemStrategy(definition).onItemUse(
                SpongeBridge.wrap(player),
                definition,
                SpongeBridge.wrap(itemStack),
                SpongeBridge.toIgnisInteraction(event),
                null);
    }

    private IgnisItemStrategy requireItemStrategy(ItemDefinition definition) {
        IgnisStrategy strategy = strategyRegistry.get(definition.getExtensionId());
        if (!(strategy instanceof IgnisItemStrategy itemStrategy)) {
            throw new IllegalStateException("Item type " + definition.getId() + " uses a non-item strategy from extension "
                    + definition.getExtensionId());
        }
        return itemStrategy;
    }
}
