package dev.rono.igniscore.sponge.support;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.EngineConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Reflection bridge to Sponge implementation internals for vanilla play packets.
 */
public final class SpongeNativePacketSupport {
    private final boolean nativePackets;
    private final Method packetSenderSend;
    private final Constructor<?> explodePacketConstructor;

    public SpongeNativePacketSupport(Logger logger) {
        Method sender = null;
        Constructor<?> explodeCtor = null;
        boolean active = false;

        try {
            Class<?> packetSender = Class.forName("org.spongepowered.common.network.PacketSender");
            sender = findSendMethod(packetSender);
            explodeCtor = findExplodePacketConstructor();
            if (sender != null && explodeCtor != null) {
                active = true;
                logger.info("Sponge native explosion packet bridge enabled.");
            }
        } catch (Throwable error) {
            logger.debug("Native explosion packet bridge unavailable: {}", error.getMessage());
        }

        this.packetSenderSend = sender;
        this.explodePacketConstructor = explodeCtor;
        this.nativePackets = active;
        if (!nativePackets) {
            logger.info("Native explosion packets unavailable; using particle fallback for protocol effects.");
        }
    }

    public boolean isAvailable() {
        return nativePackets;
    }

    public void sendFakeExplosion(ServerPlayer player, double x, double y, double z, float power) {
        if (!nativePackets) {
            return;
        }
        try {
            Object packet = explodePacketConstructor.newInstance(
                    x, y, z, power, Collections.emptyList(), 0f, 0f, 0f);
            EngineConnection connection = player.connection();
            packetSenderSend.invoke(null, connection, packet);
        } catch (Throwable error) {
            // Caller falls back to particles.
        }
    }

    private static Method findSendMethod(Class<?> packetSender) {
        for (Method method : packetSender.getMethods()) {
            if (!"sendTo".equals(method.getName()) || method.getParameterCount() != 2) {
                continue;
            }
            Class<?>[] params = method.getParameterTypes();
            if (EngineConnection.class.isAssignableFrom(params[0])) {
                return method;
            }
        }
        return null;
    }

    private static Constructor<?> findExplodePacketConstructor() throws ClassNotFoundException {
        Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.ClientboundExplodePacket");
        for (Constructor<?> constructor : packetClass.getConstructors()) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length == 8
                    && params[0] == double.class
                    && params[1] == double.class
                    && params[2] == double.class
                    && params[3] == float.class
                    && List.class.isAssignableFrom(params[4])
                    && params[5] == float.class
                    && params[6] == float.class
                    && params[7] == float.class) {
                return constructor;
            }
        }
        return null;
    }
}
