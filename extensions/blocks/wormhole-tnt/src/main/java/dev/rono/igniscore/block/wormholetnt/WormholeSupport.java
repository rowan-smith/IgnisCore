package dev.rono.igniscore.block.wormholetnt;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class WormholeSupport {
    private WormholeSupport() {
    }

    static void ripBlocks(IgnisStrategyContext ctx, IgnisWorld world, IgnisLocation center, int radius, double chance) {

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) {
                        continue;
                    }

                    IgnisLocation location = center.add(x, y, z);
                    String material = world.getBlockMaterialKey(location);
                    if ("air".equals(material) || "barrier".equals(material)) {
                        continue;
                    }

                    if (Math.random() < chance) {
                        world.spawnFallingBlock(location.add(0.5, 0, 0.5), material);
                        world.setBlockMaterialKey(location, "air");
                    }
                }
            }
        }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

