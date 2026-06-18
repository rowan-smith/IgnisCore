package dev.rono.igniscore.integration.npc;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisNpcService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Citizens NPC integration. No fallback — disabled when Citizens is absent.
 */
public final class CitizensNpcService implements IgnisNpcService {
    private final Logger logger;
    private final boolean enabled;
    private final Method getNpcRegistry;
    private final Method createNpc;
    private final Method getEntity;
    private final Method destroy;
    private final Method getNavigator;
    private final Method setTarget;

    @Inject
    public CitizensNpcService(Plugin plugin) {
        this.logger = plugin.getLogger();
        Method registry = null;
        Method create = null;
        Method entity = null;
        Method destroyMethod = null;
        Method navigator = null;
        Method target = null;
        boolean active = false;

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            try {
                Class<?> citizensApi = Class.forName("net.citizensnpcs.api.CitizensAPI");
                Class<?> npcRegistryClass = Class.forName("net.citizensnpcs.api.npc.NPCRegistry");
                Class<?> npcClass = Class.forName("net.citizensnpcs.api.npc.NPC");
                Class<?> navigatorClass = Class.forName("net.citizensnpcs.api.ai.Navigator");

                registry = citizensApi.getMethod("getNPCRegistry");
                create = npcRegistryClass.getMethod("createNPC", org.bukkit.entity.EntityType.class, String.class);
                entity = npcClass.getMethod("getEntity");
                destroyMethod = npcClass.getMethod("destroy");
                navigator = npcClass.getMethod("getNavigator");
                target = navigatorClass.getMethod("setTarget", org.bukkit.entity.Entity.class);
                active = true;
                logger.info("NPC integration enabled via Citizens.");
            } catch (Throwable error) {
                logger.warning("Citizens present but API unavailable: " + error.getMessage());
            }
        } else {
            logger.info("Citizens not found. NPC integration disabled.");
        }

        this.enabled = active;
        this.getNpcRegistry = registry;
        this.createNpc = create;
        this.getEntity = entity;
        this.destroy = destroyMethod;
        this.getNavigator = navigator;
        this.setTarget = target;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String providerName() {
        return enabled ? "Citizens" : "unavailable";
    }

    @Override
    public Object spawnNpc(IgnisLocation location, String displayName) {
        if (!enabled) {
            return null;
        }
        try {
            Object registry = getNpcRegistry.invoke(null);
            Object npc = createNpc.invoke(registry, org.bukkit.entity.EntityType.PLAYER, displayName);
            Method spawn = npc.getClass().getMethod("spawn", org.bukkit.Location.class);
            spawn.invoke(npc, BukkitBridge.toBukkit(location));
            return npc;
        } catch (ReflectiveOperationException error) {
            logger.warning("Failed to spawn Citizens NPC: " + error.getMessage());
            return null;
        }
    }

    @Override
    public void setTarget(Object npcHandle, IgnisPlayer targetPlayer) {
        if (!enabled || npcHandle == null) {
            return;
        }
        try {
            Object navigator = getNavigator.invoke(npcHandle);
            setTarget.invoke(navigator, BukkitBridge.unwrap(targetPlayer));
        } catch (ReflectiveOperationException error) {
            logger.warning("Failed to set Citizens NPC target: " + error.getMessage());
        }
    }

    @Override
    public void remove(Object npcHandle) {
        if (!enabled || npcHandle == null) {
            return;
        }
        try {
            destroy.invoke(npcHandle);
        } catch (ReflectiveOperationException error) {
            logger.warning("Failed to remove Citizens NPC: " + error.getMessage());
        }
    }
}
