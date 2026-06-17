package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final FortuneCookieMakerBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new FortuneCookieMakerBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(80).combustible(false)
                .build();
    }

    @Override
    public void onPlacedInteract(BlockDefinition definition,
                                 IgnisLocation location,
                                 IgnisPlayer player,
                                 dev.rono.igniscore.api.port.IgnisInteraction interaction,
                                 IgnisItem heldItem,
                                 CustomBlockAction action) {
        behavior.onPlacedInteract(definition, location, player, interaction, heldItem, action);
    }
    @Override
    public void onPlaced(BlockDefinition definition, IgnisLocation location) {
        behavior.onPlaced(definition, location);
    }
    @Override
    public void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        behavior.onPlacedBreak(definition, location);
    }

}
