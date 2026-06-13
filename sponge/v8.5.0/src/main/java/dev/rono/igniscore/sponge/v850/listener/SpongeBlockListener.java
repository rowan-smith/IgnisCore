package dev.rono.igniscore.sponge.v850.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.sponge.v850.adapter.SpongeBridge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SpongeBlockListener {
    private final BlockManager blockManager;
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public SpongeBlockListener(BlockManager blockManager,
                               IgnisStrategyRegistry strategyRegistry) {
        this.blockManager = blockManager;
        this.strategyRegistry = strategyRegistry;
    }

    @Listener(order = Order.LATE)
    public void onInteractBlock(InteractBlockEvent.Secondary event, @First ServerPlayer player) {
        handleInteract(event, player, IgnisInteraction.RIGHT_CLICK_BLOCK);
    }

    @Listener(order = Order.LATE)
    public void onPrimaryInteractBlock(InteractBlockEvent.Primary event, @First ServerPlayer player) {
        handleInteract(event, player, IgnisInteraction.LEFT_CLICK_BLOCK);
    }

    private void handleInteract(InteractBlockEvent event, ServerPlayer player, IgnisInteraction interaction) {
        BlockDefinition definition = getPlacedDefinition(event.block());
        if (definition == null) {
            return;
        }

        IgnisBlockStrategy strategy = requireBlockStrategy(definition);
        CustomBlockAction action = strategy.onPlacedClick(
                definition,
                SpongeBridge.toIgnis(event.block().location().orElseThrow()),
                SpongeBridge.wrap(player),
                interaction,
                null);
        if (action == CustomBlockAction.NONE) {
            return;
        }

        if (event instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }

        if (action == CustomBlockAction.OPEN) {
            strategy.onPlacedInteract(
                    definition,
                    SpongeBridge.toIgnis(event.block().location().orElseThrow()),
                    SpongeBridge.wrap(player),
                    interaction,
                    null,
                    action);
        }
    }

    private IgnisBlockStrategy requireBlockStrategy(BlockDefinition definition) {
        return strategyRegistry.requireBlockStrategy(definition.getExtensionId(), definition.getId());
    }

    private BlockDefinition getPlacedDefinition(org.spongepowered.api.block.BlockSnapshot block) {
        String typeId = blockManager.getPlacedBlockType(block.location()
                .map(SpongeBridge::toIgnis)
                .orElse(null));
        if (typeId == null) {
            return null;
        }
        return blockManager.getBlockTypes().get(typeId);
    }
}
