package dev.rono.igniscore.block.nuke;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class NukeSupport {
    private NukeSupport() {
    }

    static void playCountdown(IgnisStrategyContext ctx, RuntimeBlockInstance instance) {

        int ticksLeft = instance.getTicksLeft();
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 160);
        int interval = ticksLeft > 80 ? 20 : ticksLeft > 40 ? 10 : ticksLeft > 15 ? 5 : 2;
        if (elapsed % interval != 0) {
            return;
        }

        IgnisLocation center = Locations.toCenter(instance.getLocation());
        float pitch = ticksLeft <= 15 ? 1.9f : ticksLeft <= 40 ? 1.45f : ticksLeft <= 80 ? 1.1f : 0.75f;
        ctx.effects().playSound(center, "BLOCK_NOTE_BLOCK_PLING", 2.0f, pitch);
        ctx.effects().playSound(center, "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
    
    }

    static void spawnFuseParticles(IgnisStrategyContext ctx, RuntimeBlockInstance instance) {

        int ticksLeft = instance.getTicksLeft();
        int interval = ticksLeft > 40 ? 10 : 4;
        if (ticksLeft % interval != 0) {
            return;
        }

        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(ctx, center);
        double intensity = ticksLeft <= 20 ? 1.0 : ticksLeft <= 60 ? 0.6 : 0.3;
        world.spawnParticle(center, "SMOKE", (int) (18 * intensity), 0.45, 0.45, 0.45, 0.02);
        world.spawnParticle(center, "FLAME", (int) (10 * intensity), 0.35, 0.35, 0.35, 0.04);
        world.spawnParticle(center, "LAVA", (int) (4 * intensity), 0.25, 0.25, 0.25, 0.0);
    
    }

    static void spawnDetonationParticles(IgnisStrategyContext ctx, IgnisWorld world, IgnisLocation center, float power) {

        double spread = Math.max(8.0, power * 0.8);
        world.spawnParticle(center, "FLASH", 1, 0, 0, 0, 0);
        world.spawnParticle(center, "EXPLOSION_EMITTER", Math.max(12, (int) (power * 0.6)),
                spread * 0.35, spread * 0.2, spread * 0.35, 0.0);
        world.spawnParticle(center, "FLAME", Math.max(300, (int) (power * 12)),
                spread, spread * 0.55, spread, 0.12);
        world.spawnParticle(center.add(0, power * 0.5, 0), "SMOKE",
                Math.max(450, (int) (power * 16)), spread * 0.8, spread * 0.75, spread * 0.8, 0.05);
        world.spawnParticle(center.add(0, power * 0.35, 0), "CLOUD",
                Math.max(300, (int) (power * 10)), spread * 0.7, spread * 0.55, spread * 0.7, 0.08);
        world.spawnParticle(center, "LAVA", Math.max(80, (int) (power * 3)),
                spread * 0.45, spread * 0.25, spread * 0.45, 0.0);
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

