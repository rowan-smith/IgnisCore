package dev.rono.igniscore.integration.hologram;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.service.IgnisHologramService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Hologram integration with DecentHolograms when present, otherwise TextDisplay entities.
 */
public final class BukkitHologramService implements IgnisHologramService {
    private final Plugin plugin;
    private final Logger logger;
    private final boolean decentHolograms;
    private final Method decentCreate;
    private final Method decentDelete;
    private final Method decentSetLines;
    private final Method decentTeleport;

    @Inject
    public BukkitHologramService(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        Method create = null;
        Method delete = null;
        Method setLines = null;
        Method teleport = null;
        boolean decent = false;

        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            try {
                Class<?> api = Class.forName("eu.decentsoftware.holograms.api.DHAPI");
                create = api.getMethod("createHologram", String.class, Location.class, List.class);
                delete = api.getMethod("removeHologram", String.class);
                setLines = api.getMethod("setHologramLines", String.class, List.class);
                teleport = api.getMethod("moveHologram", String.class, Location.class);
                decent = true;
                logger.info("Hologram integration enabled via DecentHolograms.");
            } catch (Throwable error) {
                logger.warning("DecentHolograms present but API unavailable: " + error.getMessage());
            }
        }

        this.decentHolograms = decent;
        this.decentCreate = create;
        this.decentDelete = delete;
        this.decentSetLines = setLines;
        this.decentTeleport = teleport;

        if (!decentHolograms) {
            logger.info("DecentHolograms not found. Holograms use TextDisplay fallback.");
        }
    }

    @Override
    public boolean isEnabled() {
        return decentHolograms || supportsTextDisplay();
    }

    @Override
    public String providerName() {
        if (decentHolograms) {
            return "DecentHolograms";
        }
        return supportsTextDisplay() ? "TextDisplay" : "unavailable";
    }

    @Override
    public Object createTextHologram(IgnisLocation location, List<String> lines) {
        if (decentHolograms) {
            return createDecentHologram(location, lines);
        }
        return createTextDisplay(location, lines);
    }

    @Override
    public void updateText(Object hologramHandle, List<String> lines) {
        if (hologramHandle instanceof DecentHandle decent) {
            try {
                decentSetLines.invoke(null, decent.id(), lines);
            } catch (ReflectiveOperationException error) {
                logger.warning("Failed to update DecentHologram: " + error.getMessage());
            }
            return;
        }
        if (hologramHandle instanceof TextDisplay display && display.isValid()) {
            display.setText(String.join("\n", lines));
            return;
        }
    }

    @Override
    public void delete(Object hologramHandle) {
        if (hologramHandle instanceof DecentHandle decent) {
            try {
                decentDelete.invoke(null, decent.id());
            } catch (ReflectiveOperationException error) {
                logger.warning("Failed to delete DecentHologram: " + error.getMessage());
            }
            return;
        }
        if (hologramHandle instanceof Entity entity && entity.isValid()) {
            entity.remove();
        }
    }

    @Override
    public void teleport(Object hologramHandle, IgnisLocation location) {
        if (hologramHandle instanceof DecentHandle decent) {
            try {
                decentTeleport.invoke(null, decent.id(), BukkitBridge.toBukkit(location));
            } catch (ReflectiveOperationException error) {
                logger.warning("Failed to teleport DecentHologram: " + error.getMessage());
            }
            return;
        }
        if (hologramHandle instanceof Entity entity && entity.isValid()) {
            entity.teleport(BukkitBridge.toBukkit(location));
        }
    }

    private Object createDecentHologram(IgnisLocation location, List<String> lines) {
        String id = "ignis-" + System.nanoTime();
        try {
            decentCreate.invoke(null, id, BukkitBridge.toBukkit(location), new ArrayList<>(lines));
            return new DecentHandle(id);
        } catch (ReflectiveOperationException error) {
            logger.warning("Failed to create DecentHologram: " + error.getMessage());
            return null;
        }
    }

    private Object createTextDisplay(IgnisLocation location, List<String> lines) {
        if (!supportsTextDisplay()) {
            return null;
        }
        Location spawn = BukkitBridge.toBukkit(location);
        if (spawn.getWorld() == null) {
            return null;
        }
        TextDisplay display = spawn.getWorld().spawn(spawn, TextDisplay.class, entity -> {
            entity.setText(String.join("\n", lines));
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setSeeThrough(true);
            entity.setShadowed(true);
            entity.setDefaultBackground(false);
        });
        return display;
    }

    private boolean supportsTextDisplay() {
        try {
            Class.forName("org.bukkit.entity.TextDisplay");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private record DecentHandle(String id) {
    }
}
