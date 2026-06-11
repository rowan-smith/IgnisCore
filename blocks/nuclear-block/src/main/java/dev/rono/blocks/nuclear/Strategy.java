package dev.rono.blocks.nuclear;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.ExplosiveStrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

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
        Location center = Locations.toCenter(location);
        center.getWorld().spawnParticle(Particle.FLAME, center, 16, 0.35, 0.35, 0.35, 0.02);
        center.getWorld().spawnParticle(Particle.SMOKE, center, 10, 0.3, 0.3, 0.3, 0.01);
    }

    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        Location center = Locations.toCenter(instance.getLocation());
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 2.0f, 0.6f);
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        ExplosiveStrategySupport.playNukeCountdown(instance);
        ExplosiveStrategySupport.spawnNukeFuseParticles(instance);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = Locations.toCenter(instance.getLocation());
        double basePower = def.getRadius() > 0 ? def.getRadius() : getCustomDouble(def, "power", 10.0);
        float finalPower = (float) (basePower * getCustomDouble(def, "multiplier", 1.0));

        instance.getData().setFloat("ignis:nuke_power", finalPower);
        instance.getData().setDouble("ignis:radiation_radius", finalPower * 2.0);

        ExplosiveStrategySupport.spawnNukeDetonationParticles(loc, finalPower);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 8.0f, 0.45f);
        loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 8.0f, 0.55f);
        loc.getWorld().createExplosion(loc, finalPower, getCustomBoolean(def, "fire", true),
                getCustomBoolean(def, "blockDamage", true));

        if (getCustomBoolean(def, "screenShake", false)) {
            loc.getWorld().getPlayers().stream()
                    .filter(p -> p.getLocation().distance(loc) < finalPower * 2)
                    .forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f));
        }
    }
}
