package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Sound;

public class NuclearStrategy extends BaseBlockBehaviorStrategy {
    public NuclearStrategy() {
        super(IgnisStrategyDescriptor.of("nuclear", "Nuclear Detonation", "1.0.0", "IgnisCore"));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile().toBuilder()
                .defaultFuse(160)
                .defaultRadius(30.0)
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .build();
    }
    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        Location center = instance.getLocation().toCenterLocation();
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 2.0f, 0.6f);
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        playNukeCountdown(instance);
        spawnNukeFuseParticles(instance);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        org.bukkit.Location loc = instance.getLocation().toCenterLocation();
        double basePower = def.getRadius() > 0 ? def.getRadius() : getCustomDouble(def, "power", 10.0);
        float finalPower = (float) (basePower * getCustomDouble(def, "multiplier", 1.0));

        // structured NBT metadata for the nuke event
        instance.getData().setFloat("ignis:nuke_power", finalPower);
        instance.getData().setDouble("ignis:radiation_radius", finalPower * 2.0);

        spawnNukeDetonationParticles(loc, finalPower);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 8.0f, 0.45f);
        loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 8.0f, 0.55f);
        loc.getWorld().createExplosion(loc, finalPower, getCustomBoolean(def, "fire", true), getCustomBoolean(def, "blockDamage", true));

        if (getCustomBoolean(def, "screenShake", false)) {
            loc.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distance(loc) < finalPower * 2)
                .forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f));
        }
    }
}
