package dev.rono.igniscore.block.xpvacuum;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class XpVacuumOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    XpVacuumOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        PlacedBlock block = event.block();
        long period = StrategySupport.customInt(block.definition(), "tickPeriod", 20);
        PlacedTickSupport.start(context, block.location(), period, () -> XpVacuumSupport.tick(context, block));
        IgnisLocation center = Locations.toCenter(block.location());
        TheatricsSupport.chime(XpVacuumSupport.worldAt(context, center), center, 1.0f);
    }
}
