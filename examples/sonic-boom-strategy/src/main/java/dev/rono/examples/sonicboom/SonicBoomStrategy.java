package dev.rono.examples.sonicboom;

import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import dev.rono.igniscore.service.CustomBlockAction;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class SonicBoomStrategy extends AbstractIgnisStrategy {
    public SonicBoomStrategy(IgnisStrategyContext context) {
        super(IgnisStrategyDescriptor.of("sonic_boom", "Sonic Boom", "1.0.0", "IgnisCore Examples",
                SonicBoomStrategyPlugin.PLUGIN_ID), context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(true)
                .defaultFuse(60)
                .defaultRadius(8.0)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.IGNITE)
                .placementSound("ENTITY_WARDEN_AMBIENT")
                .igniteSound("ENTITY_WARDEN_SONIC_CHARGE")
                .displayScale(1.15)
                .build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        Location center = instance.getLocation().toCenterLocation();
        center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 1, 0.2, 0.2, 0.2, 0.0);
        if (instance.getTicksLeft() % 15 == 0) {
            center.getWorld().playSound(center, Sound.ENTITY_WARDEN_HEARTBEAT, 1.2f, 0.8f);
        }
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition definition = instance.getDefinition();
        Location center = instance.getLocation().toCenterLocation();
        float power = (float) getCustomDouble(definition, "power", 6.0);

        center.getWorld().playSound(center, Sound.ENTITY_WARDEN_SONIC_BOOM, 2.0f, 1.0f);
        center.getWorld().spawnParticle(Particle.SONIC_BOOM, center, 3, 1.0, 0.5, 1.0, 0.0);
        center.getWorld().createExplosion(center, power, false, getCustomBoolean(definition, "blockDamage", true));
    }
}
