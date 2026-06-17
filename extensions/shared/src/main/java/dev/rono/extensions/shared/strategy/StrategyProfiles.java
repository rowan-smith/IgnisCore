package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.strategy.StrategyProfile;

public final class StrategyProfiles {
    private StrategyProfiles() {
    }

    public static StrategyProfile explosiveProfile() {
        return StrategyProfile.combustible(80, 4.0);
    }
}
