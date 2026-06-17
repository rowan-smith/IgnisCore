package dev.rono.igniscore.block.cropaccelerator;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class CropAcceleratorListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    CropAcceleratorListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "cropRadius", 4);
          BlockScanSupport.bonemealRadius(world, center, radius);
          world.playSound(center, "ITEM_BONE_MEAL_USE", 0.7f, 1.1f);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                long period = StrategySupport.customInt(event.block().definition(), "tickPeriod", 20);
                PlacedTickSupport.start(context, event.block().location(), period, () -> tick(event.block().definition(), event.block().location()));
                IgnisLocation center = Locations.toCenter(event.block().location());
                TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
    }
}
