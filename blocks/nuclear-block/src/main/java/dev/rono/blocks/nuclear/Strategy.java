package dev.rono.blocks.nuclear;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(160)
                .defaultRadius(30.0)
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .build();
    }

    @Override
    public void onStaticPlace(BlockDefinition definition, Location location) {
        Location center = location.toCenterLocation();
        StrategySupport.spawnParticles(center, Particle.FLAME, 16, 0.35, 0.35, 0.35, 0.02);
        StrategySupport.spawnParticles(center, Particle.SMOKE, 10, 0.3, 0.3, 0.3, 0.01);
    }

    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        Location center = instance.getLocation().toCenterLocation();
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 2.0f, 0.6f);
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        playCountdown(instance);
        spawnFuseParticles(instance);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        float finalPower = StrategySupport.resolvePower(def, 10.0);

        instance.getData().setFloat("ignis:nuke_power", finalPower);
        instance.getData().setDouble("ignis:radiation_radius", finalPower * 2.0);

        spawnDetonationParticles(loc, finalPower);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 8.0f, 0.45f);
        loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 8.0f, 0.55f);
        StrategySupport.createExplosion(loc, finalPower, getCustomBoolean(def, "fire", true),
                getCustomBoolean(def, "blockDamage", true));

        if (getCustomBoolean(def, "screenShake", false)) {
            loc.getWorld().getPlayers().stream()
                    .filter(p -> p.getLocation().distance(loc) < finalPower * 2)
                    .forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f));
        }
    }

    private void playCountdown(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int elapsed = StrategySupport.elapsedFuseTicks(instance, 160);
        int interval = ticksLeft > 80 ? 20 : ticksLeft > 40 ? 10 : ticksLeft > 15 ? 5 : 2;
        if (elapsed % interval != 0) {
            return;
        }

        Location center = instance.getLocation().toCenterLocation();
        float pitch = ticksLeft <= 15 ? 1.9f : ticksLeft <= 40 ? 1.45f : ticksLeft <= 80 ? 1.1f : 0.75f;
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f, pitch);
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
    }

    private void spawnFuseParticles(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int interval = ticksLeft > 40 ? 10 : 4;
        if (ticksLeft % interval != 0) {
            return;
        }

        Location center = instance.getLocation().toCenterLocation();
        double intensity = ticksLeft <= 20 ? 1.0 : ticksLeft <= 60 ? 0.6 : 0.3;
        StrategySupport.spawnParticles(center, Particle.SMOKE, (int) (18 * intensity), 0.45, 0.45, 0.45, 0.02);
        StrategySupport.spawnParticles(center, Particle.FLAME, (int) (10 * intensity), 0.35, 0.35, 0.35, 0.04);
        StrategySupport.spawnParticles(center, Particle.LAVA, (int) (4 * intensity), 0.25, 0.25, 0.25, 0.0);
    }

    private void spawnDetonationParticles(Location center, float power) {
        World world = center.getWorld();
        double spread = Math.max(8.0, power * 0.8);
        world.spawnParticle(Particle.FLASH, center, 1, 0, 0, 0, 0, Color.WHITE);
        StrategySupport.spawnParticles(center, Particle.EXPLOSION_EMITTER, Math.max(12, (int) (power * 0.6)),
                spread * 0.35, spread * 0.2, spread * 0.35, 0.0);
        StrategySupport.spawnParticles(center, Particle.FLAME, Math.max(300, (int) (power * 12)),
                spread, spread * 0.55, spread, 0.12);
        StrategySupport.spawnParticles(center.clone().add(0, power * 0.5, 0), Particle.SMOKE,
                Math.max(450, (int) (power * 16)), spread * 0.8, spread * 0.75, spread * 0.8, 0.05);
        StrategySupport.spawnParticles(center.clone().add(0, power * 0.35, 0), Particle.CLOUD,
                Math.max(300, (int) (power * 10)), spread * 0.7, spread * 0.55, spread * 0.7, 0.08);
        StrategySupport.spawnParticles(center, Particle.LAVA, Math.max(80, (int) (power * 3)),
                spread * 0.45, spread * 0.25, spread * 0.45, 0.0);
    }
}
