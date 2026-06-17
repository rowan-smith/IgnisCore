package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;

/**
 * Single entry point for services injected into extension strategies.
 *
 * <p>Extension code should use this context (not {@link dev.rono.igniscore.api.IgnisCoreAPI})
 * to avoid crossing classloader boundaries. Prefer the short accessor methods
 * ({@link #scheduler()}, {@link #nbt()}, …) for readability; the {@code get*} methods
 * remain for backward compatibility.</p>
 *
 * <h2>Capability access</h2>
 * <ul>
 *   <li>{@link #scheduler()} — delayed and repeating tasks</li>
 *   <li>{@link #nbt()} — item/entity persistent data</li>
 *   <li>{@link #effects()} — sounds, particles, fake explosions</li>
 *   <li>{@link #protocol()} — optional client protocol integration</li>
 *   <li>{@link #extensions()} — inventories, drop collectors, spectate hooks</li>
 * </ul>
 *
 * @see AbstractIgnisBlockStrategy
 * @see AbstractIgnisItemStrategy
 */
public final class IgnisStrategyContext {
    private final IgnisScheduler scheduler;
    private final IgnisNbtService nbtService;
    private final IgnisProtocolService protocolService;
    private final IgnisEffectService effectService;
    private final ExtensionSupport extensionSupport;

    /**
     * @param scheduler platform-neutral task scheduler
     * @param nbtService item/entity NBT access
     * @param protocolService optional protocol integration
     * @param effectService particles, sounds, and visual effects
     * @param extensionSupport runtime hooks for inventories and drop collection
     */
    public IgnisStrategyContext(IgnisScheduler scheduler,
                                IgnisNbtService nbtService,
                                IgnisProtocolService protocolService,
                                IgnisEffectService effectService,
                                ExtensionSupport extensionSupport) {
        this.scheduler = scheduler;
        this.nbtService = nbtService;
        this.protocolService = protocolService;
        this.effectService = effectService;
        this.extensionSupport = extensionSupport;
    }

    /** @return task scheduler */
    public IgnisScheduler scheduler() {
        return scheduler;
    }

    /** @return item and entity NBT service */
    public IgnisNbtService nbt() {
        return nbtService;
    }

    /** @return visual and audio effect service */
    public IgnisEffectService effects() {
        return effectService;
    }

    /** @return optional protocol integration (may report {@link IgnisProtocolService#isEnabled()} false) */
    public IgnisProtocolService protocol() {
        return protocolService;
    }

    /** @return extension runtime hooks (inventories, drop collectors, world resolve) */
    public ExtensionSupport extensions() {
        return extensionSupport;
    }

    /** @return task scheduler (alias for {@link #scheduler()}) */
    public IgnisScheduler getScheduler() {
        return scheduler;
    }

    /** @return item and entity NBT service (alias for {@link #nbt()}) */
    public IgnisNbtService getNbtService() {
        return nbtService;
    }

    /** @return optional protocol integration (alias for {@link #protocol()}) */
    public IgnisProtocolService getProtocolService() {
        return protocolService;
    }

    /** @return visual and audio effect service (alias for {@link #effects()}) */
    public IgnisEffectService getEffectService() {
        return effectService;
    }

    /** @return extension runtime hooks (alias for {@link #extensions()}) */
    public ExtensionSupport getExtensionSupport() {
        return extensionSupport;
    }
}
