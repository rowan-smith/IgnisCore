package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.service.CustomBlockAction;

public final class StrategyProfiles {
    private StrategyProfiles() {
    }

    public static StrategyProfile explosiveProfile() {
        return StrategyProfile.builder()
                .combustible(true)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.IGNITE)
                .defaultFuse(80)
                .defaultRadius(4.0)
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .igniteSound("ITEM_FLINTANDSTEEL_USE")
                .displayScale(1.01)
                .build();
    }
}
