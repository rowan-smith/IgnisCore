package dev.rono.igniscore.resourcepack;

import java.util.Locale;
import java.util.Map;

/**
 * Maps igniscore block face keys to vanilla resource-pack texture paths.
 */
final class VanillaTexturePaths {
    private static final Map<String, String> FACE_TO_MODEL_KEY = Map.of(
            "top", "top",
            "bottom", "bottom",
            "side", "side",
            "side-1", "north",
            "side-2", "east",
            "side-3", "south",
            "side-4", "west"
    );

    private VanillaTexturePaths() {
    }

    static String blockModelTextureKey(String faceKey) {
        return FACE_TO_MODEL_KEY.get(faceKey);
    }

    static String blockTexturePath(String blockId, String faceKey) {
        String suffix = switch (faceKey) {
            case "top" -> "_top";
            case "bottom" -> "_bottom";
            case "side", "side-1", "side-2", "side-3", "side-4" -> "_side";
            default -> "_" + faceKey.replace('-', '_');
        };
        return "minecraft:block/" + blockId.toLowerCase(Locale.ROOT) + suffix;
    }

    static String itemTexturePath(String itemId) {
        return "minecraft:item/" + itemId.toLowerCase(Locale.ROOT);
    }
}
