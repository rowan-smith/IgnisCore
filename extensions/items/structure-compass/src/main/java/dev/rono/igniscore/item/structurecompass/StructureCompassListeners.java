package dev.rono.igniscore.item.structurecompass;

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

final class StructureCompassListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    StructureCompassListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation loc = player.getEyeLocation();
        String heading = nbtService.getItemString(item, "ignis:compass_heading");
        if (heading == null || heading.isBlank()) {
            heading = "north";
            nbtService.setItemString(item, "ignis:compass_heading", heading);
        }
        float yaw = switch (heading.toLowerCase()) {
            case "east" -> 90f;
            case "south" -> 180f;
            case "west" -> 270f;
            default -> 0f;
        };
        player.sendMessage("<aqua>Structure compass bearing: <white>" + heading + "</white> (" + (int) yaw + "°)</aqua>");
        TheatricsSupport.pulseRing(world, loc, 3.0, "END_ROD");
        world.playSound(loc, "ITEM_LODESTONE_COMPASS_LOCK", 0.8f, 1.0f);
    }


    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
    }
}
