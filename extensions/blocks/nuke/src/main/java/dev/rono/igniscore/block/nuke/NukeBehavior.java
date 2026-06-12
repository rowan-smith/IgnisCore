package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class NukeBehavior {
    private final IgnisStrategyContext context;

    NukeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onStaticPlace(IgnisLocation location) {
        IgnisLocation center = Locations.toCenter(location);
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center, "FLAME", 16, 0.35, 0.35, 0.35, 0.02);
        world.spawnParticle(center, "SMOKE", 10, 0.3, 0.3, 0.3, 0.01);
    }

    void onPlace(RuntimeBlockInstance instance) {
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        context.getEffectService().playSound(center, "BLOCK_BEACON_ACTIVATE", 2.0f, 0.6f);
    }

    void onTick(RuntimeBlockInstance instance) {
        playCountdown(instance);
        spawnFuseParticles(instance);
    }

    void onTrigger(RuntimeBlockInstance instance, BlockDefinition def) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        float finalPower = StrategySupport.resolvePower(def, 10.0);
        IgnisWorld world = worldAt(loc);

        instance.getData().setDouble("ignis:nuke_power", finalPower);
        instance.getData().setDouble("ignis:radiation_radius", finalPower * 2.0);

        spawnDetonationParticles(world, loc, finalPower);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 8.0f, 0.45f);
        world.playSound(loc, "ENTITY_LIGHTNING_BOLT_THUNDER", 8.0f, 0.55f);
        StrategySupport.createExplosion(world, loc, def, 10.0,
                StrategySupport.customBoolean(def, "fire", true));

        if (StrategySupport.customBoolean(def, "screenShake", false)) {
            for (var player : world.getPlayersNear(loc, finalPower * 2)) {
                player.getWorld().playSound(player.getLocation(), "ENTITY_GENERIC_EXPLODE", 2.0f, 0.5f);
            }
        }
    }

    private void playCountdown(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int elapsed = StrategySupport.elapsedFuseTicks(instance, 160);
        int interval = ticksLeft > 80 ? 20 : ticksLeft > 40 ? 10 : ticksLeft > 15 ? 5 : 2;
        if (elapsed % interval != 0) {
            return;
        }

        IgnisLocation center = Locations.toCenter(instance.getLocation());
        float pitch = ticksLeft <= 15 ? 1.9f : ticksLeft <= 40 ? 1.45f : ticksLeft <= 80 ? 1.1f : 0.75f;
        context.getEffectService().playSound(center, "BLOCK_NOTE_BLOCK_PLING", 2.0f, pitch);
        context.getEffectService().playSound(center, "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
    }

    private void spawnFuseParticles(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int interval = ticksLeft > 40 ? 10 : 4;
        if (ticksLeft % interval != 0) {
            return;
        }

        IgnisLocation center = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(center);
        double intensity = ticksLeft <= 20 ? 1.0 : ticksLeft <= 60 ? 0.6 : 0.3;
        world.spawnParticle(center, "SMOKE", (int) (18 * intensity), 0.45, 0.45, 0.45, 0.02);
        world.spawnParticle(center, "FLAME", (int) (10 * intensity), 0.35, 0.35, 0.35, 0.04);
        world.spawnParticle(center, "LAVA", (int) (4 * intensity), 0.25, 0.25, 0.25, 0.0);
    }

    private void spawnDetonationParticles(IgnisWorld world, IgnisLocation center, float power) {
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

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
