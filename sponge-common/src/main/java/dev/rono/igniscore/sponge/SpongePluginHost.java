package dev.rono.igniscore.sponge;

import dev.rono.igniscore.api.port.PlatformAdapter;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.plugin.PluginContainer;

/**
 * Version-neutral host surface implemented by each Sponge API line entrypoint.
 */
public interface SpongePluginHost {

    PluginContainer container();

    Game game();

    Logger getLogger();

    PlatformAdapter platformAdapter();

    default Class<?> hostClass() {
        return getClass();
    }
}
