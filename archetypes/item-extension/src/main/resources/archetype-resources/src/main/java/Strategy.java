package ${package};

import dev.rono.igniscore.api.config.ExtensionConfigs;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

public class Strategy extends AbstractIgnisItemStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {
        if (!"use".equals(definition.interactionAction(action))) {
            return;
        }
        var throwable = ExtensionConfigs.throwable(definition);
        var world = player.getWorld();
        var location = player.getEyeLocation();
        world.playSound(location, "ENTITY_SNOWBALL_THROW", 1.0f, 1.0f);
        StrategySupport.createExplosion(world, location, definition, throwable.power(), throwable.fire());
        item.setAmount(item.getAmount() - 1);
    }
}
