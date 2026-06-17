package dev.rono.igniscore.block.compostteabrewer;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class CompostTeaBrewerOnBlockInteractListener implements OnBlockInteractListener {
    private final CompostTeaBrewerRuntime runtime;

    CompostTeaBrewerOnBlockInteractListener(CompostTeaBrewerRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        if (event.heldItem() != null && !event.heldItem().isAir() && ProcessingGuiSupport.matches(event.heldItem(), "splash_potion", "lingering_potion")) {
            IgnisWorld world = CompostTeaBrewerSupport.worldAt(runtime, event.block().location());
            BlockScanSupport.bonemealRadius(world, Locations.toCenter(event.block().location()), StrategySupport.customInt(event.block().definition(), "cropRadius", 4));
            event.heldItem().setAmount(event.heldItem().getAmount() - 1);
            event.player().sendMessage("<green>Compost tea splashes growth over nearby crops.</green>");
        }
        runtime.registry.openBlock(event.player(), event.block().location());
    }
}

