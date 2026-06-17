package dev.rono.igniscore.block.chickencoopcache;

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
import java.util.Collection;
import net.kyori.adventure.text.Component;

final class ChickenCoopCacheListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    ChickenCoopCacheListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "chicken-coop-cache");
    }

    private boolean collectEggs(IgnisLocation coopLocation, Collection<IgnisItem> drops) {
        var gui = registry.blockGui(coopLocation);
        if (gui == null) {
            return false;
        }
        boolean stored = false;
        var inventory = gui.inventory();
        var iterator = drops.iterator();
        while (iterator.hasNext()) {
            IgnisItem drop = iterator.next();
            if (drop == null || drop.isAir() || !isEgg(drop)) {
                continue;
            }
            if (storeItem(inventory, drop)) {
                iterator.remove();
                stored = true;
            }
        }
        return stored;
    }

    private boolean storeItem(dev.rono.igniscore.api.port.IgnisInventory inventory, IgnisItem stack) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            IgnisItem existing = inventory.getItem(slot);
            if (existing == null || existing.isAir()) {
                inventory.setItem(slot, stack);
                return true;
            }
            if (existing.getMaterialKey().equals(stack.getMaterialKey()) && existing.getAmount() < 64) {
                int move = Math.min(stack.getAmount(), 64 - existing.getAmount());
                existing.setAmount(existing.getAmount() + move);
                stack.setAmount(stack.getAmount() - move);
                inventory.setItem(slot, existing);
                return stack.getAmount() <= 0;
            }
        }
        return false;
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        for (Object entity : world.getNearbyEntities(center, StrategySupport.customDouble(definition, "coopRadius", 6.0))) {
            if (entity.getClass().getSimpleName().toLowerCase().contains("chicken")) {
                TheatricsSupport.sparkle(world, center, "EGG_CRACK", 2);
                world.playSound(center, "ENTITY_CHICKEN_EGG", 0.3f, 1.2f);
                var gui = registry.blockGui(location);
                if (gui != null) {
                    storeItem(gui.inventory(), context.extensions().createItem("egg", 1));
                }
                break;
            }
        }
    }

    private boolean isEgg(IgnisItem item) {
        return "egg".equalsIgnoreCase(item.getMaterialKey());
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Chicken Coop") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.registerBlock(event.block().location(), title(event.block().definition()), 3);
                context.extensions().registerDropCollector(event.block().location(), (breakLocation, drops) -> collectEggs(event.block().location(), drops));
                long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 100);
                PlacedTickSupport.start(context, event.block().location(), period, () -> tick(event.block().definition(), event.block().location()));
                TheatricsSupport.chime(worldAt(event.block().location()), Locations.toCenter(event.block().location()), 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
                context.extensions().unregisterDropCollector(event.block().location());
                registry.unregister(event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() == CustomBlockAction.OPEN) {
                    registry.openBlock(event.player(), event.block().location());
                }
    }
}
