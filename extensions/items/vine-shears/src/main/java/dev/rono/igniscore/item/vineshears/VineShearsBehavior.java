package dev.rono.igniscore.item.vineshears;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

final class VineShearsBehavior {
    private final IgnisStrategyContext context;

    VineShearsBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        if (clickedBlock == null) {
            return;
        }
        IgnisWorld world = player.getWorld();
        int max = StrategySupport.customInt(definition.getCustomData(), "maxVines", 32);
        Set<String> visited = new HashSet<>();
        Queue<IgnisLocation> queue = new ArrayDeque<>();
        queue.add(clickedBlock.getLocation());
        int cut = 0;
        while (!queue.isEmpty() && cut < max) {
            IgnisLocation loc = queue.poll();
            String key = (int) loc.x() + ":" + (int) loc.y() + ":" + (int) loc.z();
            if (!visited.add(key)) {
                continue;
            }
            String material = world.getBlockMaterialKey(loc).toLowerCase();
            if (!material.contains("vine") && !material.contains("weeping") && !material.contains("twisting")) {
                continue;
            }
            world.setBlockMaterialKey(loc, "air");
            cut++;
            queue.add(loc.add(1, 0, 0));
            queue.add(loc.add(-1, 0, 0));
            queue.add(loc.add(0, 1, 0));
            queue.add(loc.add(0, -1, 0));
            queue.add(loc.add(0, 0, 1));
            queue.add(loc.add(0, 0, -1));
        }
        player.sendMessage("<gray>Cut <white>" + cut + "</white> vine blocks.</gray>");
        TheatricsSupport.sparkle(world, clickedBlock.getLocation(), "CLOUD", 6);
        item.setAmount(item.getAmount() - 1);
    }
}
