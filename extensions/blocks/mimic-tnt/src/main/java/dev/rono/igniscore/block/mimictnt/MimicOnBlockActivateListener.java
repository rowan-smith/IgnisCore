package dev.rono.igniscore.block.mimictnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockActivateEvent;
import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class MimicOnBlockActivateListener implements OnBlockActivateListener {
    private final IgnisStrategyContext context;

    MimicOnBlockActivateListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockActivate(BlockActivateEvent event) {
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = MimicSupport.worldAt(context, loc);

        if (event.instance().getDisplayEntity() != null) {
            world.removeEntity(event.instance().getDisplayEntity());
            event.instance().setDisplayEntity(null);
        }

        int mimicCount = StrategySupport.customInt(event.definition(), "mimicCount", 8);
        double horizontalPower = StrategySupport.customDouble(event.definition(), "mimicHorizontalPower", 1.0);
        double verticalPower = StrategySupport.customDouble(event.definition(), "mimicVerticalPower", 0.5);

        int totalCount = mimicCount + 1;
        int realIndex = (int) (Math.random() * totalCount);

        for (int i = 0; i < totalCount; i++) {
            Object tnt = world.spawnEntity("TNT", loc);
            int fuse = ExplosionSupport.fuse(event.definition(), 80) + (int) (Math.random() * 40 - 20);
            boolean real = i == realIndex;
            world.configurePrimedTnt(
                    tnt,
                    Math.max(10, fuse),
                    real ? (float) StrategySupport.customDouble(event.definition(), "power", 4.0) : 0f,
                    real && StrategySupport.customBoolean(event.definition(), "fire", false));
            world.setEntityVelocity(
                    tnt,
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower + (Math.random() * 0.4),
                    (Math.random() - 0.5) * horizontalPower);
        }

        event.instance().setTicksLeft(0);
    }
}

