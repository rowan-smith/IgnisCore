package dev.rono.igniscore.sponge.integration.hologram;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.sponge.SpongePluginHost;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.display.BillboardTypes;
import org.spongepowered.api.entity.display.TextDisplay;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;

import java.util.List;

/**
 * Hologram integration using TextDisplay entities on Sponge.
 */
public final class SpongeHologramService implements IgnisHologramService {
    private final Logger logger;
    private final boolean textDisplaySupported;

    @Inject
    public SpongeHologramService(SpongePluginHost plugin) {
        this.logger = plugin.getLogger();
        boolean supported = false;
        try {
            EntityTypes.TEXT_DISPLAY.get();
            supported = true;
            logger.info("Hologram integration enabled via TextDisplay.");
        } catch (Throwable error) {
            logger.warn("TextDisplay entities unavailable: {}", error.getMessage());
        }
        this.textDisplaySupported = supported;
    }

    @Override
    public boolean isEnabled() {
        return textDisplaySupported;
    }

    @Override
    public String providerName() {
        return textDisplaySupported ? "TextDisplay" : "unavailable";
    }

    @Override
    public Object createTextHologram(IgnisLocation location, List<String> lines) {
        if (!textDisplaySupported) {
            return null;
        }
        ServerWorld world = resolveWorld(location);
        if (world == null) {
            return null;
        }
        ServerLocation spawn = SpongeBridge.toSponge(location, world);
        Entity entity = spawn.createEntity(EntityTypes.TEXT_DISPLAY.get());
        Component text = Component.text(String.join("\n", lines));
        entity.offer(Keys.DISPLAY_NAME, text);
        entity.offer(Keys.BILLBOARD_TYPE, BillboardTypes.CENTER.get());
        entity.offer(Keys.SEE_THROUGH_BLOCKS, true);
        entity.offer(Keys.HAS_TEXT_SHADOW, true);
        entity.offer(Keys.HAS_DEFAULT_BACKGROUND, false);
        spawn.spawnEntity(entity);
        return entity;
    }

    @Override
    public void updateText(Object hologramHandle, List<String> lines) {
        if (hologramHandle instanceof TextDisplay display && !display.isRemoved()) {
            display.offer(Keys.DISPLAY_NAME, Component.text(String.join("\n", lines)));
            return;
        }
        if (hologramHandle instanceof Entity entity && !entity.isRemoved()) {
            entity.offer(Keys.DISPLAY_NAME, Component.text(String.join("\n", lines)));
        }
    }

    @Override
    public void delete(Object hologramHandle) {
        if (hologramHandle instanceof Entity entity && !entity.isRemoved()) {
            entity.remove();
        }
    }

    @Override
    public void teleport(Object hologramHandle, IgnisLocation location) {
        if (!(hologramHandle instanceof Entity entity) || entity.isRemoved()) {
            return;
        }
        ServerWorld world = resolveWorld(location);
        if (world == null) {
            return;
        }
        entity.setLocation(SpongeBridge.toSponge(location, world));
    }

    private ServerWorld resolveWorld(IgnisLocation location) {
        var worlds = SpongeRuntimeHolder.server().worldManager().worlds();
        if (worlds.isEmpty()) {
            return null;
        }
        ServerWorld defaultWorld = worlds.iterator().next();
        return SpongeBridge.resolveWorld(location, defaultWorld);
    }
}
