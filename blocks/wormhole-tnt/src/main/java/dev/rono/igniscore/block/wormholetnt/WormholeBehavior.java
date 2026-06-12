package dev.rono.igniscore.block.wormholetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

final class WormholeBehavior {
    void onTick(RuntimeBlockInstance instance) {
        Location loc = Locations.toCenter(instance.getLocation());
        int ticksLeft = instance.getTicksLeft();
        BlockDefinition def = instance.getDefinition();

        int ripRadius = StrategySupport.customInt(def, "wormholeRipRadius", 3);
        int ripStartTicks = StrategySupport.customInt(def, "wormholeRipStartTicks", 60);
        double ripChance = StrategySupport.customDouble(def, "wormholeRipChance", 0.02);

        if (ticksLeft < ripStartTicks) {
            ripBlocks(loc, ripRadius, ripChance);
        }

        double radius = 8.0 + (StrategySupport.fuse(def, 100) - ticksLeft) * 0.1;

        loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
            if (entity.getUniqueId().equals(instance.getDisplayEntity() != null
                    ? instance.getDisplayEntity().getUniqueId() : null)) {
                return;
            }

            Vector pull = loc.toVector().subtract(entity.getLocation().toVector());
            double dist = pull.length();
            if (dist > 0.1) {
                pull.normalize().multiply(0.15 * (1.0 - dist / radius));
                entity.setVelocity(entity.getVelocity().add(pull));
            }
        });

        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 15, 0.3, 0.3, 0.3, 0.2);
        if (ticksLeft % 5 == 0) {
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_AMBIENT, 1.0f,
                    0.5f + (float) (StrategySupport.fuse(def, 100) - ticksLeft) / 80.0f);
        }
    }

    void onTrigger(RuntimeBlockInstance instance) {
        Location loc = Locations.toCenter(instance.getLocation());
        BlockDefinition def = instance.getDefinition();

        StrategySupport.createExplosion(loc, def, 10.0, false);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 5, 2, 2, 2, 0);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
    }

    private void ripBlocks(Location center, int radius, double chance) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) {
                        continue;
                    }

                    Location location = center.clone().add(x, y, z);
                    Block block = location.getBlock();

                    if (block.getType().isAir() || block.getType() == Material.BARRIER) {
                        continue;
                    }
                    if (block.getType().getHardness() < 0) {
                        continue;
                    }

                    if (Math.random() < chance) {
                        FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(
                                location.clone().add(0.5, 0, 0.5), block.getBlockData());
                        fallingBlock.setDropItem(false);
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}
