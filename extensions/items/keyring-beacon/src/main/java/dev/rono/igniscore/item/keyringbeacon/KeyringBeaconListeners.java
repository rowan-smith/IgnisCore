package dev.rono.igniscore.item.keyringbeacon;

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

final class KeyringBeaconListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    KeyringBeaconListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation loc = event.player().getEyeLocation();
                int slots = StrategySupport.customInt(event.definition().getCustomData(), "beaconSlots", 3);
                int index = nbtService.getItemInt(event.item(), "ignis:beacon_index", 0) % Math.max(1, slots);
                String key = "ignis:beacon_" + index;
                nbtService.setItemString(event.item(), key, loc.x() + "," + loc.y() + "," + loc.z());
                nbtService.setItemInt(event.item(), "ignis:beacon_index", index + 1);
                event.player().sendMessage("<aqua>Beacon slot <white>" + index + "</white> marked.</aqua>");
                TheatricsSupport.pulseRing(world, loc, 2.0, "END_ROD");
                world.playSound(loc, "BLOCK_BEACON_ACTIVATE", 0.7f, 1.0f);
            }
    }
}
