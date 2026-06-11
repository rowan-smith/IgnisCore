package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

public final class ExplosiveStrategySupport {
    private ExplosiveStrategySupport() {
    }

    public static float resolvePower(BlockDefinition definition, double defaultPower) {
        double base = definition.getRadius() > 0 ? definition.getRadius() : customDouble(definition, "power", defaultPower);
        return (float) (base * customDouble(definition, "multiplier", 1.0));
    }

    public static void createExplosion(org.bukkit.Location location, BlockDefinition definition, double defaultPower, boolean defaultFire) {
        location.getWorld().createExplosion(
                location,
                resolvePower(definition, defaultPower),
                customBoolean(definition, "fire", defaultFire),
                customBoolean(definition, "blockDamage", true)
        );
    }

    public static double customDouble(BlockDefinition definition, String key, double defaultValue) {
        Object value = definition.getCustomData().get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return defaultValue;
    }

    public static boolean customBoolean(BlockDefinition definition, String key, boolean defaultValue) {
        Object value = definition.getCustomData().get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return defaultValue;
    }

    public static void playNukeCountdown(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int fuse = Math.max(1, instance.getDefinition().getFuse());
        int elapsed = Math.max(0, fuse - ticksLeft);
        int interval = ticksLeft > 80 ? 20 : ticksLeft > 40 ? 10 : ticksLeft > 15 ? 5 : 2;
        if (elapsed % interval != 0) {
            return;
        }

        Location center = instance.getLocation().toCenterLocation();
        float pitch = ticksLeft <= 15 ? 1.9f : ticksLeft <= 40 ? 1.45f : ticksLeft <= 80 ? 1.1f : 0.75f;
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f, pitch);
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
    }

    public static void spawnNukeFuseParticles(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int interval = ticksLeft > 40 ? 10 : 4;
        if (ticksLeft % interval != 0) {
            return;
        }

        Location center = instance.getLocation().toCenterLocation();
        World world = center.getWorld();
        double intensity = ticksLeft <= 20 ? 1.0 : ticksLeft <= 60 ? 0.6 : 0.3;
        world.spawnParticle(Particle.SMOKE, center, (int) (18 * intensity), 0.45, 0.45, 0.45, 0.02);
        world.spawnParticle(Particle.FLAME, center, (int) (10 * intensity), 0.35, 0.35, 0.35, 0.04);
        world.spawnParticle(Particle.LAVA, center, (int) (4 * intensity), 0.25, 0.25, 0.25, 0.0);
    }

    public static void spawnNukeDetonationParticles(Location center, float power) {
        World world = center.getWorld();
        double spread = Math.max(8.0, power * 0.8);
        world.spawnParticle(Particle.FLASH, center, 1, 0, 0, 0, 0, Color.WHITE);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, Math.max(12, (int) (power * 0.6)), spread * 0.35, spread * 0.2, spread * 0.35, 0.0);
        world.spawnParticle(Particle.FLAME, center, Math.max(300, (int) (power * 12)), spread, spread * 0.55, spread, 0.12);
        world.spawnParticle(Particle.SMOKE, center.clone().add(0, power * 0.5, 0), Math.max(450, (int) (power * 16)), spread * 0.8, spread * 0.75, spread * 0.8, 0.05);
        world.spawnParticle(Particle.CLOUD, center.clone().add(0, power * 0.35, 0), Math.max(300, (int) (power * 10)), spread * 0.7, spread * 0.55, spread * 0.7, 0.08);
        world.spawnParticle(Particle.LAVA, center, Math.max(80, (int) (power * 3)), spread * 0.45, spread * 0.25, spread * 0.45, 0.0);
    }

    public static void spawnSpiderStormBurst(Location center, float power) {
        World world = center.getWorld();
        double spread = Math.max(3.0, power * 0.45);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.25f);
        world.playSound(center, Sound.ENTITY_SPIDER_AMBIENT, 3.0f, 0.65f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 3, 1.0, 0.5, 1.0, 0.0);
        world.spawnParticle(Particle.SMOKE, center, 160, spread, 1.4, spread, 0.04);
        world.spawnParticle(Particle.CLOUD, center, 120, spread * 0.8, 1.1, spread * 0.8, 0.08);
        world.spawnParticle(Particle.BLOCK, center, 90, spread * 0.5, 0.8, spread * 0.5, 0.02, org.bukkit.Material.COBWEB.createBlockData());
    }
}
