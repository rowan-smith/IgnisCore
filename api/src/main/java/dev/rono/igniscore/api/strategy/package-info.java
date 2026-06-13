/**
 * Strategy interfaces and helpers for block/item extension behavior.
 *
 * <p>Extension JARs provide a class listed in {@code *-extension.yml} that extends
 * {@link dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy} or
 * {@link dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy}. Block placed-phase callbacks
 * are {@link dev.rono.igniscore.api.strategy.IgnisBlockStrategy#onPlaced},
 * {@link dev.rono.igniscore.api.strategy.IgnisBlockStrategy#onPlacedInteract}, and
 * {@link dev.rono.igniscore.api.strategy.IgnisBlockStrategy#onPlacedBreak}.</p>
 */
package dev.rono.igniscore.api.strategy;
