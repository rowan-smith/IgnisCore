package dev.rono.igniscore.sponge.listener;

import dev.rono.igniscore.manager.BlockManager;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.block.BlockSnapshot;

import static org.junit.jupiter.api.Assertions.assertNull;

class SpongeBlockListenerBehaviorTest {

    @Test
    void returnsNullWhenSnapshotHasNoResolvableLocation() throws Exception {
        SpongeBlockListener listener = new SpongeBlockListener(
                new NoopBlockManager(),
                null,
                null,
                null,
                null,
                null);

        assertNull(invokePlacedDefinition(listener, BlockSnapshot.empty()));
    }

    private static Object invokePlacedDefinition(SpongeBlockListener listener, BlockSnapshot snapshot)
            throws Exception {
        var method = SpongeBlockListener.class.getDeclaredMethod("getPlacedDefinition", BlockSnapshot.class);
        method.setAccessible(true);
        return method.invoke(listener, snapshot);
    }

    private static final class NoopBlockManager extends BlockManager {
        NoopBlockManager() {
            super(null, null, null, null, null, null, null);
        }
    }
}
