package dev.rono.igniscore.sponge.v1900.service;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.sponge.v1900.renderer.NoopBlockVisualRenderer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class NoopServicesBehaviorTest {

    @Test
    void protocolServiceReportsDisabledAndIgnoresExplosions() {
        SpongeNoopProtocolService service = new SpongeNoopProtocolService();

        assertFalse(service.isEnabled());
        assertDoesNotThrow(() -> service.sendFakeExplosion(
                new IgnisLocation("world", 0, 0, 0),
                4.0f,
                List.<IgnisPlayer>of()));
    }

    @Test
    void effectServiceIgnoresFakeExplosionAndPreview() {
        SpongeNoopEffectService service = new SpongeNoopEffectService(null);

        assertDoesNotThrow(() -> {
            service.playFakeExplosion(new IgnisLocation("world", 0, 0, 0), 4.0f, List.of());
            service.showBlockPreview(null, new IgnisLocation("world", 0, 0, 0), "stone");
        });
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
}
