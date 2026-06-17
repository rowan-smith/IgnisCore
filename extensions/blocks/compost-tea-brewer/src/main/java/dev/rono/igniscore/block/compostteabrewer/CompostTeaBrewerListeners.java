package dev.rono.igniscore.block.compostteabrewer;

import dev.rono.extensions.shared.gui.BlockStorageRegistry;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
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
import net.kyori.adventure.text.Component;

final class CompostTeaBrewerListeners implements OnBlockPlaceListener, OnBlockBreakListener, OnBlockInteractListener {
    private static final int BONE_SLOT = 11;
    private static final int BOTTLE_SLOT = 15;
    private static final int OUTPUT_SLOT = 17;

    private final IgnisStrategyContext context;
    private final BlockStorageRegistry registry;

    CompostTeaBrewerListeners(IgnisStrategyContext context) {
        this.context = context;
        this.registry = new BlockStorageRegistry(context, "compost-tea-brewer");
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        var gui = registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(BONE_SLOT), "bone_meal")
                || !ProcessingGuiSupport.matches(inventory.getItem(BOTTLE_SLOT), "glass_bottle", "potion")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, BONE_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, BOTTLE_SLOT);
        ProcessingGuiSupport.setOutput(context.extensions(), inventory, OUTPUT_SLOT, "splash_potion", 1);
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 6);
        world.playSound(center, "ITEM_BOTTLE_FILL", 0.7f, 1.0f);
    }

    private Component title(BlockDefinition definition) {
        return definition.getTitle() == null ? Component.text("Compost Tea Brewer") : definition.getTitle();
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                registry.registerBlock(event.block().location(), title(event.block().definition()), 3);
                PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 45),
                        () -> tick(event.block().definition(), event.block().location()));
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
                registry.unregister(event.block().location());
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
                if (event.action() != CustomBlockAction.OPEN) {
                    return;
                }
                if (event.heldItem() != null && !event.heldItem().isAir() && ProcessingGuiSupport.matches(event.heldItem(), "splash_potion", "lingering_potion")) {
                    IgnisWorld world = worldAt(event.block().location());
                    BlockScanSupport.bonemealRadius(world, Locations.toCenter(event.block().location()), StrategySupport.customInt(event.block().definition(), "cropRadius", 4));
                    event.heldItem().setAmount(event.heldItem().getAmount() - 1);
                    event.player().sendMessage("<green>Compost tea splashes growth over nearby crops.</green>");
                }
                registry.openBlock(event.player(), event.block().location());
    }
}
