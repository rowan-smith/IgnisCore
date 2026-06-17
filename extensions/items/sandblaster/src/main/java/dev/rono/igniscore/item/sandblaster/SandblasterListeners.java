package dev.rono.igniscore.item.sandblaster;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class SandblasterListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    SandblasterListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                if (event.clickedBlock() == null) {
                    return;
                }
                IgnisWorld world = event.player().getWorld();
                IgnisLocation center = event.clickedBlock().getLocation();
                int radius = StrategySupport.customInt(event.definition().getCustomData(), "etchRadius", 1);
                int etched = 0;
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            IgnisLocation probe = center.add(x, y, z);
                            String material = world.getBlockMaterialKey(probe).toLowerCase();
                            if (material.contains("smooth_stone") || material.equals("stone")) {
                                world.setBlockMaterialKey(probe, "chiseled_stone_bricks");
                                etched++;
                            }
                        }
                    }
                }
                if (etched == 0) {
                    event.player().sendMessage("<gray>No smooth stone in range to etch.</gray>");
                    return;
                }
                TheatricsSupport.sparkle(world, center.add(0.5, 0.5, 0.5), "CLOUD", 10);
                world.playSound(center, "BLOCK_STONE_BREAK", 0.7f, 1.1f);
                event.player().sendMessage("<gray>Etched <white>" + etched + "</white> blocks.</gray>");
                event.item().setAmount(event.item().getAmount() - 1);
            }
    }
}
