package dev.rono.igniscore.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.event.BlockActivateEvent;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockClickEvent;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockClickListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.ItemUseSupport;
import dev.rono.igniscore.api.strategy.PlacedClickSupport;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.service.StrategyProfileResolver;

@Singleton
public class StrategyEventPublisher {
    private final IgnisEventBusImpl eventBus;
    private final StrategyProfileResolver profileResolver;

    @Inject
    public StrategyEventPublisher(IgnisEventBusImpl eventBus, StrategyProfileResolver profileResolver) {
        this.eventBus = eventBus;
        this.profileResolver = profileResolver;
    }

    public void fireBlockPlace(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {
        BlockPlaceEvent event = new BlockPlaceEvent(definition, location, placedFrom);
        eventBus.dispatch(definition.getExtensionId(), OnBlockPlaceListener.class,
                listener -> listener.onBlockPlace(event));
    }

    public CustomBlockAction fireBlockClick(BlockDefinition definition,
                                            IgnisLocation location,
                                            IgnisPlayer player,
                                            IgnisInteraction interaction,
                                            IgnisItem heldItem) {
        CustomBlockAction defaultResult = resolveDefaultClick(definition, interaction, heldItem);
        BlockClickEvent event = new BlockClickEvent(definition, location, player, interaction, heldItem, defaultResult);
        eventBus.dispatch(definition.getExtensionId(), OnBlockClickListener.class,
                listener -> listener.onBlockClick(event));
        return event.result();
    }

    public void fireBlockInteract(BlockDefinition definition,
                                  IgnisLocation location,
                                  IgnisPlayer player,
                                  IgnisInteraction interaction,
                                  IgnisItem heldItem,
                                  CustomBlockAction action) {
        BlockInteractEvent event = new BlockInteractEvent(definition, location, player, interaction, heldItem, action);
        eventBus.dispatch(definition.getExtensionId(), OnBlockInteractListener.class,
                listener -> listener.onBlockInteract(event));
    }

    public void fireBlockBreak(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem) {
        BlockBreakEvent event = new BlockBreakEvent(definition, location, droppedItem);
        eventBus.dispatch(definition.getExtensionId(), OnBlockBreakListener.class,
                listener -> listener.onBlockBreak(event));
    }

    public void fireBlockActivate(RuntimeBlockInstance instance) {
        BlockActivateEvent event = new BlockActivateEvent(instance);
        eventBus.dispatch(instance.getDefinition().getExtensionId(), OnBlockActivateListener.class,
                listener -> listener.onBlockActivate(event));
    }

    public void fireBlockTick(RuntimeBlockInstance instance) {
        BlockTickEvent event = new BlockTickEvent(instance);
        eventBus.dispatch(instance.getDefinition().getExtensionId(), OnBlockTickListener.class,
                listener -> listener.onBlockTick(event));
    }

    public void fireBlockTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockTriggerEvent event = new BlockTriggerEvent(instance, triggerContext);
        eventBus.dispatch(instance.getDefinition().getExtensionId(), OnBlockTriggerListener.class,
                listener -> listener.onBlockTrigger(event));
    }

    public void fireItemClick(IgnisPlayer player,
                              ItemDefinition definition,
                              IgnisItem item,
                              IgnisInteraction interaction,
                              dev.rono.igniscore.api.port.IgnisBlock clickedBlock) {
        String actionToken = ItemUseSupport.actionFor(definition, interaction).orElse(null);
        ItemClickEvent event = new ItemClickEvent(player, definition, item, interaction, clickedBlock, actionToken);
        eventBus.dispatch(definition.getExtensionId(), OnItemClickListener.class,
                listener -> listener.onItemClick(event));
    }

    private CustomBlockAction resolveDefaultClick(BlockDefinition definition,
                                                  IgnisInteraction interaction,
                                                  IgnisItem heldItem) {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(definition.getBehaviorConfig());
        StrategyProfile profile = profileResolver.resolve(definition);
        StrategyProfile merged = behavior.merge(profile);
        if (!behavior.isEmpty()) {
            return behavior.resolve(interaction, merged, materialKey(heldItem));
        }
        return PlacedClickSupport.resolve(merged, interaction, heldItem);
    }

    private static String materialKey(IgnisItem heldItem) {
        if (heldItem == null || heldItem.isAir()) {
            return "AIR";
        }
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }
}
