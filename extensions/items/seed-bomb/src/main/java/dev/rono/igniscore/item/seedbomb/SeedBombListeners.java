package dev.rono.igniscore.item.seedbomb;

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

final class SeedBombListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    SeedBombListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation eye = event.player().getEyeLocation();
                double speed = StrategySupport.customDouble(event.definition().getCustomData(), "throwSpeed", 1.1);
                Object bomb = world.spawnProjectile("egg", eye, event.player(), 0, 0, speed);
                event.item().setAmount(event.item().getAmount() - 1);
                if (bomb == null) {
                    return;
                }
                int fuse = StrategySupport.customInt(event.definition().getCustomData(), "scatterDelayTicks", 15);
                int radius = StrategySupport.customInt(event.definition().getCustomData(), "scatterRadius", 3);
                context.scheduler().runLater(eye, () -> {
                    IgnisLocation impact = world.isEntityValid(bomb) ? world.getEntityLocation(bomb) : eye;
                    if (impact == null) {
                        return;
                    }
                    if (world.isEntityValid(bomb)) {
                        world.removeEntity(bomb);
                    }
                    String[] plants = {"short_grass", "dandelion", "poppy", "cornflower"};
                    for (int x = -radius; x <= radius; x++) {
                        for (int z = -radius; z <= radius; z++) {
                            IgnisLocation soil = impact.add(x, -1, z);
                            IgnisLocation spot = impact.add(x, 0, z);
                            String below = world.getBlockMaterialKey(soil).toLowerCase();
                            String above = world.getBlockMaterialKey(spot).toLowerCase();
                            if ((below.contains("dirt") || below.contains("grass")) && above.contains("air")) {
                                world.setBlockMaterialKey(spot, plants[(x + z + radius) % plants.length]);
                            }
                        }
                    }
                    TheatricsSupport.sparkle(world, impact, "HAPPY_VILLAGER", 12);
                    world.playSound(impact, "BLOCK_GRASS_PLACE", 0.8f, 1.2f);
                }, fuse);
            }
    }
}
