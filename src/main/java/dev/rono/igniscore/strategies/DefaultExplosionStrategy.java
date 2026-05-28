package dev.rono.igniscore.strategies;

import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Sound;

public class DefaultExplosionStrategy extends BaseBlockBehaviorStrategy {
    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        org.bukkit.Location loc = instance.getLocation();
        float power = (float) (getCustomDouble(def, "power", 4.0) * getCustomDouble(def, "multiplier", 1.0));

        // Store runtime state in NBT for potential use by other systems
        instance.getData().setFloat("ignis:blast_power", power);

        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        loc.getWorld().createExplosion(loc, power, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
    }
}
