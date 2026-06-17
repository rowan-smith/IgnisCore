package dev.rono.igniscore.block.repairstationblock;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class RepairStationBlockOnBlockInteractListener implements OnBlockInteractListener {
    private final IgnisStrategyContext context;

    RepairStationBlockOnBlockInteractListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = RepairStationBlockSupport.worldAt(context, event.block().location());
        IgnisLocation center = Locations.toCenter(event.block().location());
        if (event.heldItem() == null || event.heldItem().isAir()) {
                event.player().sendMessage("<yellow>Hold a damaged item to repair.</yellow>");
                return;
            }
            int repairAmount = StrategySupport.customInt(event.block().definition(), "repairAmount", 25);
            event.player().sendMessage("<green>Repair station restored <white>" + repairAmount + "</white> durability.</green>");
            TheatricsSupport.sparkle(world, center, "ENCHANT", 16);
            world.playSound(center, "BLOCK_ANVIL_USE", 0.8f, 1.0f);
    }
}

