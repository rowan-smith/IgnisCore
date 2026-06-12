package dev.rono.igniscore.sponge.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.service.SpongeBlockManager;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class SpongeBlockListener {
    private final SpongeBlockManager blockManager;
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public SpongeBlockListener(SpongeBlockManager blockManager,
                               IgnisStrategyRegistry strategyRegistry) {
        this.blockManager = blockManager;
        this.strategyRegistry = strategyRegistry;
    }

    @Listener(order = Order.LATE)
    public void onInteractBlock(InteractBlockEvent.Secondary event, @First ServerPlayer player) {
        BlockDefinition definition = getPlacedDefinition(event.block());
        if (definition == null) {
            return;
        }

        event.setCancelled(true);
        requireBlockStrategy(definition).onStaticInteract(
                definition,
                SpongeBridge.toIgnis(event.block().location().orElseThrow()),
                SpongeBridge.wrap(player),
                dev.rono.igniscore.api.CustomBlockAction.OPEN);
    }

    @Listener(order = Order.LATE)
    public void onPrimaryInteractBlock(InteractBlockEvent.Primary event, @First ServerPlayer player) {
        BlockDefinition definition = getPlacedDefinition(event.block());
        if (definition == null) {
            return;
        }

        if (event instanceof org.spongepowered.api.event.Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }

    private IgnisBlockStrategy requireBlockStrategy(BlockDefinition definition) {
        IgnisStrategy strategy = strategyRegistry.get(definition.getExtensionId());
        if (!(strategy instanceof IgnisBlockStrategy blockStrategy)) {
            throw new IllegalStateException("Block type " + definition.getId() + " uses a non-block strategy from extension "
                    + definition.getExtensionId());
        }
        return blockStrategy;
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
