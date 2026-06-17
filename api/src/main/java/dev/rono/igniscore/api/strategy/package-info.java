/**
 * Strategy interfaces and helpers for block/item extension behavior.
 *
 * <p>Extension JARs provide a class listed in {@code *-extension.yml} that extends
 * {@link dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy} or
 * {@link dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy}. Subscribe to lifecycle
 * events in the strategy constructor via {@link dev.rono.igniscore.api.strategy.IgnisStrategyContext#eventBus()}.</p>
 */
package dev.rono.igniscore.api.strategy;
