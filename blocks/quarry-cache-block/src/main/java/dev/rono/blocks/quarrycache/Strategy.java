package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final QuarryCacheRegistry registry;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.registry = new QuarryCacheRegistry(context);
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
    public void onStaticPlace(BlockDefinition definition, Location location, ItemStack placedFrom) {
        registry.register(location, definition, placedFrom);
    }

    @Override
    public void onStaticInteract(BlockDefinition definition, Location location, Player player, CustomBlockAction action) {
        if (action == CustomBlockAction.OPEN) {
            registry.openGui(player, location);
        }
    }

    @Override
    public void onStaticBreak(BlockDefinition definition, Location location, ItemStack droppedItem) {
        registry.handleBreak(location, droppedItem);
    }
}
