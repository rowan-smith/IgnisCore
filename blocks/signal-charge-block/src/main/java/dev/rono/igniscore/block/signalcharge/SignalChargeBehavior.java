package dev.rono.igniscore.block.signalcharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Sound;

final class SignalChargeBehavior {
    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        Location loc = Locations.toCenter(instance.getLocation());
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.9f);
        StrategySupport.createExplosion(loc, def, 4.0, false);
    }
}
