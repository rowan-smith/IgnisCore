package dev.rono.igniscore.sponge.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.ExtensionSupportService;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import org.spongepowered.api.block.transaction.Operations;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent;
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SpongeExtensionSupportListener {
    private final BlockManager blockManager;
    private final ExtensionSupportService extensionSupport;

    @Inject
    public SpongeExtensionSupportListener(BlockManager blockManager, ExtensionSupportService extensionSupport) {
        this.blockManager = blockManager;
        this.extensionSupport = extensionSupport;
    }

    @Listener(order = Order.LATE)
    public void onBlockBreak(ChangeBlockEvent.All event, @First ServerPlayer player) {
        if (!extensionSupport.hasDropCollectors()) {
            return;
        }

        event.transactions(Operations.BREAK.get()).forEach(transaction -> {
            transaction.original().location().ifPresent(location -> {
                if (blockManager.getPlacedBlockType(SpongeBridge.toIgnis(location)) != null) {
                    return;
                }
                if (player.gameMode().get().equals(GameModes.CREATIVE.get())) {
                    return;
                }
                transaction.invalidate();
            });
        });
    }

    @Listener(order = Order.LATE)
    public void onItemSpawn(SpawnEntityEvent event) {
        if (!extensionSupport.hasDropCollectors()) {
            return;
        }

        for (var entity : event.entities()) {
            if (!(entity instanceof Item itemEntity)) {
                continue;
            }
            ItemStack stack = itemEntity.item().get().createStack();
            List<IgnisItem> drops = new ArrayList<>();
            drops.add(SpongeBridge.wrap(stack.copy()));
            extensionSupport.tryCollect(SpongeBridge.toIgnis(itemEntity.serverLocation()), drops);
            if (drops.isEmpty()) {
                itemEntity.remove();
                continue;
            }
            ItemStack remaining = SpongeBridge.unwrap(drops.getFirst());
            if (remaining != null && remaining.quantity() != stack.quantity()) {
                itemEntity.item().set(remaining.createSnapshot());
            }
        }
    }

    @Listener(order = Order.LATE)
    public void onInventoryClick(ClickContainerEvent event) {
        Inventory top = event.container();
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(top);
        if (customInventory == null) {
            return;
        }

        for (var transaction : event.transactions()) {
            int rawSlot = top.slots().indexOf(transaction.slot());
            if (rawSlot >= 0 && rawSlot < top.capacity() && customInventory.isSeparatorSlot(rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }

        if (event instanceof ClickContainerEvent.Shift) {
            ItemStack moving = event.slot().flatMap(slot -> slot.peek()).orElse(ItemStack.empty());
            if (!moving.isEmpty() && !customInventory.accepts(SpongeBridge.wrap(moving))) {
                event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.LATE)
    public void onInventoryClickPersist(ClickContainerEvent event) {
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(event.container());
        if (customInventory != null) {
            customInventory.onChange();
        }
    }

    @Listener(order = Order.LATE)
    public void onInventoryDrag(ClickContainerEvent.Drag event) {
        Inventory top = event.container();
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(top);
        if (customInventory == null) {
            return;
        }

        for (var slot : event.slots()) {
            int rawSlot = top.slots().indexOf(slot);
            if (rawSlot >= 0 && rawSlot < top.capacity() && customInventory.isSeparatorSlot(rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Listener(order = Order.LATE)
    public void onInventoryClose(InteractContainerEvent.Close event) {
        IgnisCustomInventory customInventory = extensionSupport.getCustomInventory(event.container());
        if (customInventory != null) {
            customInventory.restoreDecorations();
            customInventory.onClose();
        }
    }
}
