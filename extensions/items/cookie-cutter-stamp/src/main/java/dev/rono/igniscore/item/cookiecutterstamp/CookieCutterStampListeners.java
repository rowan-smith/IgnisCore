package dev.rono.igniscore.item.cookiecutterstamp;

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

final class CookieCutterStampListeners implements OnItemClickListener {
    private static final String[] SHAPES = {"heart", "star", "tree", "moon"};

    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    CookieCutterStampListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        if (nbtService == null) {
            player.sendMessage("<red>NBT integration required for cookie shapes.</red>");
            return;
        }
        int index = nbtService.getItemInt(item, "ignis:shape_index", 0) % SHAPES.length;
        String shape = SHAPES[index];
        nbtService.setItemString(item, "ignis:cookie_shape", shape);
        nbtService.setItemInt(item, "ignis:shape_index", index + 1);
        player.sendMessage("<light_purple>Stamped cookie shape: <white>" + shape + "</white></light_purple>");
        IgnisWorld world = player.getWorld();
        TheatricsSupport.sparkle(world, player.getLocation(), "HEART", 6);
        world.playSound(player.getLocation(), "BLOCK_WOOL_PLACE", 0.8f, 1.4f);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
    }
}
