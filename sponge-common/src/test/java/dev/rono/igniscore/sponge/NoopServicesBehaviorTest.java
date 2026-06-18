package dev.rono.igniscore.sponge;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.sponge.renderer.NoopBlockVisualRenderer;
import dev.rono.igniscore.sponge.service.SpongeProtocolService;
import dev.rono.igniscore.sponge.support.SpongeNativePacketSupport;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoopServicesBehaviorTest {

    @Test
    void protocolServiceReportsEnabledWithParticleProviderInTestRuntime() {
        SpongeProtocolService service = new SpongeProtocolService(new TestSpongePluginHost());

        assertTrue(service.isEnabled());
        assertDoesNotThrow(() -> service.sendFakeExplosion(
                new IgnisLocation("world", 0, 0, 0),
                4.0f,
                List.of()));
    }

    @Test
    void nativePacketSupportReportsUnavailableInTestRuntime() {
        SpongeNativePacketSupport support = new SpongeNativePacketSupport(LogManager.getLogger("test"));

        assertFalse(support.isAvailable());
    }

    @Test
    void visualRendererReturnsNullDisplayAndAcceptsLifecycleCalls() {
        NoopBlockVisualRenderer renderer = new NoopBlockVisualRenderer();
        IgnisLocation location = new IgnisLocation("world", 1, 64, 1);

        assertNull(renderer.spawnStaticDisplay(location, null));
        assertDoesNotThrow(() -> {
            renderer.spawnAnimatedDisplay(null);
            renderer.updateAnimation(null);
            renderer.removeDisplay(null);
            renderer.removeStaticDisplay(null);
        });
    }

    private static final class TestSpongePluginHost implements SpongePluginHost {
        @Override
        public org.spongepowered.plugin.PluginContainer container() {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.spongepowered.api.Game game() {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.apache.logging.log4j.Logger getLogger() {
            return LogManager.getLogger("test");
        }

        @Override
        public dev.rono.igniscore.api.port.PlatformAdapter platformAdapter() {
            return null;
        }
    }
}
