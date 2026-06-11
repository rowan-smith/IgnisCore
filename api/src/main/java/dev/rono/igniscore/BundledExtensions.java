package dev.rono.igniscore;

import java.util.List;

/**
 * Single source of truth for bundled extension JAR names.
 */
public final class BundledExtensions {
    public static final List<String> BLOCK_JARS = List.of(
            "nuclear-block.jar",
            "wormhole-block.jar",
            "phantom-block.jar",
            "erupting-block.jar",
            "mimic-block.jar",
            "tunneling-block.jar",
            "entity-block.jar"
    );

    private BundledExtensions() {
    }
}
