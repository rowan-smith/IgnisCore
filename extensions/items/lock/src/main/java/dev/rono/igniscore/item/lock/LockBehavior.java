package dev.rono.igniscore.item.lock;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class LockBehavior {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    LockBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.getNbtService();
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation loc = player.getEyeLocation();
        boolean locked = nbtService.getItemBoolean(item, "ignis:locked", false);
        nbtService.setItemBoolean(item, "ignis:locked", !locked);
        player.sendMessage(locked ? "<green>Lock disengaged.</green>" : "<red>Lock engaged.</red>");
        TheatricsSupport.sparkle(world, loc, locked ? "WAX_OFF" : "WAX_ON", 8);
        world.playSound(loc, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.8f, locked ? 1.2f : 0.8f);
    }

}
