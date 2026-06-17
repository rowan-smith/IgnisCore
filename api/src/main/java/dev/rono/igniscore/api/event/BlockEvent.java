package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.PlacedBlock;

/**
 * Common shape for block lifecycle events.
 *
 * <p>{@link #definition()} is the extension block type from config.yml.
 * {@link #block()} is the placed block instance in the world (type + position).</p>
 */
public interface BlockEvent {

    /**
     * @return the extension block type definition
     */
    default BlockDefinition definition() {
        return block().definition();
    }

    /**
     * @return the placed block instance this event refers to
     */
    PlacedBlock block();

    default ExtensionConfig config() {
        return definition().getCustomConfig();
    }
}
