package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.bukkit.Location;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(false)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.OPEN)
                .placementSound("BLOCK_CHEST_PLACE")
                .build();
    }

    @Override
    public void onStaticPlace(BlockDefinition definition, Location location) {
        context.getQuarryCacheService().register(location, definition);
    }
}
