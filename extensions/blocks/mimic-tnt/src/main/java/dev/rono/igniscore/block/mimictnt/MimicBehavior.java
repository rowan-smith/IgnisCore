package dev.rono.igniscore.block.mimictnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class MimicBehavior {
    private final IgnisStrategyContext context;

    MimicBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlace(RuntimeBlockInstance instance, BlockDefinition def) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        if (instance.getDisplayEntity() != null) {
            world.removeEntity(instance.getDisplayEntity());
            instance.setDisplayEntity(null);
        }

        int mimicCount = StrategySupport.customInt(def, "mimicCount", 8);
        double horizontalPower = StrategySupport.customDouble(def, "mimicHorizontalPower", 1.0);
        double verticalPower = StrategySupport.customDouble(def, "mimicVerticalPower", 0.5);

        int totalCount = mimicCount + 1;
        int realIndex = (int) (Math.random() * totalCount);

        for (int i = 0; i < totalCount; i++) {
            Object tnt = world.spawnEntity("TNT", loc);
            int fuse = ExplosionSupport.fuse(def, 80) + (int) (Math.random() * 40 - 20);
            boolean real = i == realIndex;
            world.configurePrimedTnt(
                    tnt,
                    Math.max(10, fuse),
                    real ? (float) StrategySupport.customDouble(def, "power", 4.0) : 0f,
                    real && StrategySupport.customBoolean(def, "fire", false));
            world.setEntityVelocity(
                    tnt,
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower + (Math.random() * 0.4),
                    (Math.random() - 0.5) * horizontalPower);
        }

        instance.setTicksLeft(0);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
