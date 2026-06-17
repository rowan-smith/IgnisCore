/**
 * Platform-neutral port interfaces (L1) that abstract Minecraft host APIs.
 *
 * <p>These types define the boundary between the shared Ignis runtime and
 * version-specific adapter modules. Implementations live in platform bootloaders
 * and adapters; extension strategies consume only these handles rather than
 * Bukkit, Sponge, or Fabric types directly.</p>
 *
 * <p>Key entry points include {@link dev.rono.igniscore.api.port.PlatformBootloader} for
 * discovery via {@link java.util.ServiceLoader}, {@link dev.rono.igniscore.api.port.PlatformAdapter}
 * as the aggregated port surface, and typed handles such as
 * {@link dev.rono.igniscore.api.port.IgnisPlayer}, {@link dev.rono.igniscore.api.port.IgnisWorld},
 * and {@link dev.rono.igniscore.api.port.IgnisItem}.</p>
 */
package dev.rono.igniscore.api.port;
