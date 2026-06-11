package dev.rono.igniscore.api.strategy;

/**
 * Entry point for drop-in strategy JARs.
 * Implement this class and reference it from {@code strategy-plugin.yml}.
 */
public interface IgnisStrategyPlugin {

    void onLoad(IgnisStrategyRegistry registry, IgnisStrategyContext context);

    default void onUnload(IgnisStrategyRegistry registry) {}
}
