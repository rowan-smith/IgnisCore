/**
 * Public IgnisCore API surface for extensions and platform integrations.
 *
 * <p>This package is the stable contract between the core runtime and extension JARs. It is
 * organized into focused subpackages:</p>
 * <ul>
 *   <li>{@link dev.rono.igniscore.api.model} — block/item definitions loaded from extension config</li>
 *   <li>{@link dev.rono.igniscore.api.strategy} — behavior strategies and registration</li>
 *   <li>{@link dev.rono.igniscore.api.port} — platform-neutral adapters for world, player, and items</li>
 *   <li>{@link dev.rono.igniscore.api.service} — NBT, protocol, and effect services</li>
 *   <li>{@link dev.rono.igniscore.api.config} — YAML parsing and behavior configuration</li>
 *   <li>{@link dev.rono.igniscore.api.extension} — extension manifest and capability metadata</li>
 * </ul>
 *
 * <p>Extension code should prefer {@link dev.rono.igniscore.api.strategy.IgnisStrategyContext} for
 * runtime services. {@link dev.rono.igniscore.api.IgnisCoreAPI} is the static entry point used by
 * platform plugins after the core binds an {@link dev.rono.igniscore.api.IgnisCoreFacade} at startup.</p>
 *
 * <p>API compatibility is expressed with {@link dev.rono.igniscore.api.IgnisApiVersion} and
 * {@link dev.rono.igniscore.api.SemVersion}; extensions declare a target {@code api-version} in
 * their manifest.</p>
 */
package dev.rono.igniscore.api;
