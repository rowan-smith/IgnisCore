package dev.rono.igniscore.item.lock;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class LockListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    LockListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation loc = event.player().getEyeLocation();
                boolean locked = nbtService.getItemBoolean(event.item(), "ignis:locked", false);
                nbtService.setItemBoolean(event.item(), "ignis:locked", !locked);
                event.player().sendMessage(locked ? "<green>Lock disengaged.</green>" : "<red>Lock engaged.</red>");
                TheatricsSupport.sparkle(world, loc, locked ? "WAX_OFF" : "WAX_ON", 8);
                world.playSound(loc, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.8f, locked ? 1.2f : 0.8f);
            }
    }
}
