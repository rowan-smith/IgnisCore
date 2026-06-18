package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.TextureFallbackReference;
import dev.rono.igniscore.loader.ExtensionResourceProvider;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves missing extension textures using {@code textures.fallback} references.
 */
final class TextureFallbackResolver {
    private static final String CATALOG_PREFIX = "/assets/igniscore/fallbacks/";

    private final ExtensionResourceProvider resourceProvider;
    private final Map<String, BlockDefinition> blockDefinitions;
    private final Map<String, ItemDefinition> itemDefinitions;

    TextureFallbackResolver(ExtensionResourceProvider resourceProvider,
                            Map<String, BlockDefinition> blockDefinitions,
                            Map<String, ItemDefinition> itemDefinitions) {
        this.resourceProvider = resourceProvider;
        this.blockDefinitions = blockDefinitions;
        this.itemDefinitions = itemDefinitions;
    }

    Optional<String> minecraftBlockTexturePath(TextureFallbackReference fallback, String faceKey) {
        if (fallback == null || !fallback.isMinecraft()) {
            return Optional.empty();
        }
        return Optional.of(VanillaTexturePaths.blockTexturePath(fallback.id(), faceKey));
    }

    Optional<String> minecraftItemTexturePath(TextureFallbackReference fallback) {
        if (fallback == null || !fallback.isMinecraft()) {
            return Optional.empty();
        }
        return Optional.of(VanillaTexturePaths.itemTexturePath(fallback.id()));
    }

    Optional<InputStream> resolveBlockFaceTexture(TextureFallbackReference fallback, String faceKey) {
        if (fallback == null) {
            return Optional.empty();
        }
        if (fallback.isMinecraft()) {
            return Optional.empty();
        }

        BlockDefinition blockDefinition = blockDefinitions.get(fallback.id());
        if (blockDefinition != null) {
            String sourceFile = blockFaceFileName(blockDefinition, faceKey);
            Optional<InputStream> blockStream = openBlockExtensionTexture(fallback.id(), sourceFile);
            if (blockStream.isPresent()) {
                return blockStream;
            }
        }

        ItemDefinition itemDefinition = itemDefinitions.get(fallback.id());
        if (itemDefinition != null) {
            Optional<InputStream> itemStream = openItemExtensionTexture(fallback.id(), itemDefinition.getIconTexture());
            if (itemStream.isPresent()) {
                return itemStream;
            }
        }

        return openCatalogTexture(fallback.id(), blockFaceFileName(faceKey));
    }

    Optional<InputStream> resolveItemIconTexture(TextureFallbackReference fallback) {
        if (fallback == null) {
            return Optional.empty();
        }
        if (fallback.isMinecraft()) {
            return Optional.empty();
        }

        Optional<InputStream> itemStream = openItemExtensionTexture(fallback.id(), "icon.png");
        if (itemStream.isPresent()) {
            return itemStream;
        }

        BlockDefinition blockDefinition = blockDefinitions.get(fallback.id());
        if (blockDefinition != null) {
            Optional<InputStream> topStream = openBlockExtensionTexture(fallback.id(), blockDefinition.getTopTexture());
            if (topStream.isPresent()) {
                return topStream;
            }
            Optional<InputStream> sideStream = openBlockExtensionTexture(fallback.id(), blockDefinition.getSideTexture());
            if (sideStream.isPresent()) {
                return sideStream;
            }
        }

        return openCatalogTexture(fallback.id(), "icon.png");
    }

    private Optional<InputStream> openBlockExtensionTexture(String extensionId, String fileName) {
        BlockDefinition definition = blockDefinitions.get(extensionId);
        if (definition == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(resourceProvider.getBlockTextureStream(definition, fileName));
    }

    private Optional<InputStream> openItemExtensionTexture(String extensionId, String fileName) {
        ItemDefinition definition = itemDefinitions.get(extensionId);
        if (definition == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(resourceProvider.getItemTextureStream(definition, fileName));
    }

    private static Optional<InputStream> openCatalogTexture(String catalogId, String fileName) {
        String resourcePath = CATALOG_PREFIX + catalogId + "/" + fileName;
        InputStream stream = TextureFallbackResolver.class.getResourceAsStream(resourcePath);
        return Optional.ofNullable(stream);
    }

    private static String blockFaceFileName(BlockDefinition definition, String faceKey) {
        return switch (faceKey) {
            case "top" -> definition.getTopTexture();
            case "bottom" -> definition.getBottomTexture();
            case "side" -> definition.getSideTexture();
            case "side-1" -> definition.getResolvedSideTexture(1);
            case "side-2" -> definition.getResolvedSideTexture(2);
            case "side-3" -> definition.getResolvedSideTexture(3);
            case "side-4" -> definition.getResolvedSideTexture(4);
            default -> faceKey + ".png";
        };
    }

    private static String blockFaceFileName(String faceKey) {
        return switch (faceKey) {
            case "top" -> "top.png";
            case "bottom" -> "bottom.png";
            case "side" -> "side.png";
            case "side-1" -> "side-1.png";
            case "side-2" -> "side-2.png";
            case "side-3" -> "side-3.png";
            case "side-4" -> "side-4.png";
            default -> faceKey + ".png";
        };
    }
}
