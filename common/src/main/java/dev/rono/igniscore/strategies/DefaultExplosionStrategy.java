package dev.rono.igniscore.strategies;

import com.google.inject.Inject;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.strategy.StrategySupport;

public class DefaultExplosionStrategy extends AbstractIgnisBlockStrategy {
    private static final double DEFAULT_POWER = 4.0;

    private final ExtensionSupport extensionSupport;

    @Inject
    public DefaultExplosionStrategy(ExtensionSupport extensionSupport) {
        super(IgnisStrategyDescriptor.of("default", "Default Explosion", "1.0.0", "IgnisCore"));
        this.extensionSupport = extensionSupport;
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(true)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.IGNITE)
                .defaultFuse(80)
                .defaultRadius(4.0)
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .igniteSound("ITEM_FLINTANDSTEEL_USE")
                .displayScale(1.01)
                .build();
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = instance.getLocation();
        float power = resolvePower(def);

        instance.getData().setDouble("ignis:blast_power", power);

        IgnisWorld world = extensionSupport.resolveWorld(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        world.createExplosion(
                loc,
                power,
                StrategySupport.customBoolean(def, "fire", false),
                StrategySupport.customBoolean(def, "blockDamage", true));
    }

    private static float resolvePower(BlockDefinition definition) {
        double base = StrategySupport.customDouble(definition, "radius", 0);
        if (base <= 0) {
            base = StrategySupport.customDouble(definition, "power", DEFAULT_POWER);
        }
        return (float) (base * StrategySupport.customDouble(definition, "multiplier", 1.0));
    }
}
