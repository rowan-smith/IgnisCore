package dev.rono.igniscore.block.wormholetnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class WormholeListeners implements OnBlockTickListener, OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    WormholeListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private void ripBlocks(IgnisWorld world, IgnisLocation center, int radius, double chance) {
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

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
                int ticksLeft = event.instance().getTicksLeft();
                BlockDefinition def = event.instance().getDefinition();

                int ripRadius = StrategySupport.customInt(def, "wormholeRipRadius", 3);
                int ripStartTicks = StrategySupport.customInt(def, "wormholeRipStartTicks", 60);
                double ripChance = StrategySupport.customDouble(def, "wormholeRipChance", 0.02);

                if (ticksLeft < ripStartTicks) {
                    ripBlocks(world, loc, ripRadius, ripChance);
                }

                double radius = 8.0 + (ExplosionSupport.fuse(def, 100) - ticksLeft) * 0.1;
                Object displayEntity = event.instance().getDisplayEntity();

                for (Object entity : world.getNearbyEntities(loc, radius)) {
                    if (displayEntity != null && entity.equals(displayEntity)) {
                        continue;
                    }
                    IgnisLocation entityLoc = world.getEntityLocation(entity);
                    if (entityLoc == null) {
                        continue;
                    }
                    double dx = loc.x() - entityLoc.x();
                    double dy = loc.y() - entityLoc.y();
                    double dz = loc.z() - entityLoc.z();
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    if (dist > 0.1) {
                        double pull = 0.15 * (1.0 - dist / radius);
                        world.setEntityVelocity(
                                entity,
                                dx / dist * pull,
                                dy / dist * pull,
                                dz / dist * pull);
                    }
                }

                world.spawnParticle(loc, "PORTAL", 15, 0.3, 0.3, 0.3, 0.2);
                if (ticksLeft % 5 == 0) {
                    world.playSound(loc, "BLOCK_BEACON_AMBIENT", 1.0f,
                            0.5f + (float) (ExplosionSupport.fuse(def, 100) - ticksLeft) / 80.0f);
                }
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
                BlockDefinition def = event.instance().getDefinition();

                ExplosionSupport.createExplosion(world, loc, def, 10.0, false);
                world.spawnParticle(loc, "EXPLOSION_EMITTER", 5, 2, 2, 2, 0);
                world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 2.0f, 0.5f);
    }
}
