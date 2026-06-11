package dev.rono.blocks.wormhole;

import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.ExplosiveStrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class Strategy extends AbstractIgnisStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(100)
                .defaultRadius(12.0)
                .build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        Location loc = instance.getLocation().toCenterLocation();
        int ticksLeft = instance.getTicksLeft();
        BlockDefinition def = instance.getDefinition();

        int ripRadius = getCustomInt(def, "wormholeRipRadius", 3);
        int ripStartTicks = getCustomInt(def, "wormholeRipStartTicks", 60);
        double ripChance = getCustomDouble(def, "wormholeRipChance", 0.02);

        if (ticksLeft < ripStartTicks) {
            ripBlocks(loc, ripRadius, ripChance);
        }

        double radius = 8.0 + (def.getFuse() - ticksLeft) * 0.1;

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
                    0.5f + (float) (def.getFuse() - ticksLeft) / 80.0f);
        }
    }

    private void ripBlocks(Location center, int radius, double chance) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z > radius * radius) {
                        continue;
                    }

                    Location l = center.clone().add(x, y, z);
                    Block b = l.getBlock();

                    if (b.getType().isAir() || b.getType() == Material.BARRIER) {
                        continue;
                    }
                    if (b.getType().getHardness() < 0) {
                        continue;
                    }

                    if (Math.random() < chance) {
                        FallingBlock fb = b.getWorld().spawnFallingBlock(l.clone().add(0.5, 0, 0.5), b.getBlockData());
                        fb.setDropItem(false);
                        b.setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        Location loc = instance.getLocation().toCenterLocation();
        BlockDefinition def = instance.getDefinition();

        float power = ExplosiveStrategySupport.resolvePower(def, 10.0);
        ExplosiveStrategySupport.createExplosion(loc, def, 10.0, false);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 5, 2, 2, 2, 0);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
    }
}
