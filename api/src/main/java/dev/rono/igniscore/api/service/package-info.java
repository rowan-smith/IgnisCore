/**
 * Cross-cutting runtime services (L3) built on top of platform ports.
 *
 * <p>Unlike L1 port handles, these interfaces provide higher-level capabilities
 * shared across strategies and extensions: NBT access, protocol packets, visual
 * effects, and similar concerns that would otherwise be duplicated in each
 * platform adapter.</p>
 *
 * <p>Services are typically obtained from
 * {@link dev.rono.igniscore.api.strategy.IgnisStrategyContext} and may report
 * feature availability (for example {@link dev.rono.igniscore.api.service.IgnisProtocolService#isEnabled()}).</p>
 */
package dev.rono.igniscore.api.service;
