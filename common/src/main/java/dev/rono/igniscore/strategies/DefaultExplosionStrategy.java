package dev.rono.igniscore.strategies;

import com.google.inject.Inject;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategySupport;

public class DefaultExplosionStrategy extends AbstractIgnisBlockStrategy implements OnBlockTriggerListener {
    private static final double DEFAULT_POWER = 4.0;

    private final ExtensionSupport extensionSupport;
    private final IgnisEventBus eventBus;

    @Inject
    public DefaultExplosionStrategy(ExtensionSupport extensionSupport, IgnisEventBus eventBus) {
        super(IgnisStrategyDescriptor.of("default", "Default Explosion", "1.0.0", "IgnisCore"));
        this.extensionSupport = extensionSupport;
        this.eventBus = eventBus;
        eventBus.subscribe(descriptor().getId(), this);
    }

    @Override
    public void bindDescriptor(IgnisStrategyDescriptor descriptor) {
        eventBus.unsubscribe(this);
        super.bindDescriptor(descriptor);
        eventBus.subscribe(descriptor().getId(), this);
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.definition();
        IgnisLocation loc = event.block().location();
        float power = resolvePower(def);

        event.instance().getData().setDouble("ignis:blast_power", power);

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
