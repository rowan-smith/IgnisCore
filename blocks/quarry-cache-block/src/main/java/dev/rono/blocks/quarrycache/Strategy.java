package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Strategy extends AbstractIgnisBlockStrategy {
    private static final QuarryCacheRegistry REGISTRY = new QuarryCacheRegistry();
    private static volatile boolean listenerRegistered;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        ensureListenerRegistered();
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
        REGISTRY.register(location, definition);
    }

    @Override
    public void onStaticInteract(BlockDefinition definition, Location location, Player player, CustomBlockAction action) {
        if (action == CustomBlockAction.OPEN) {
            REGISTRY.openGui(player, location);
        }
    }

    @Override
    public void onStaticBreak(BlockDefinition definition, Location location) {
        REGISTRY.dropContents(location);
    }

    private void ensureListenerRegistered() {
        if (listenerRegistered) {
            return;
        }
        synchronized (Strategy.class) {
            if (listenerRegistered) {
                return;
            }
            context.getPlugin().getServer().getPluginManager()
                    .registerEvents(new QuarryCacheListener(REGISTRY), context.getPlugin());
            listenerRegistered = true;
        }
    }
}
