package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ExtensionDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.LoadedExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class ResourcePackFingerprint {
    private ResourcePackFingerprint() {
    }

    public static String compute(Map<String, BlockDefinition> blocks,
                                 Map<String, ItemDefinition> items,
                                 List<? extends LoadedExtension<? extends ExtensionDefinition>> blockExtensions,
                                 List<? extends LoadedExtension<? extends ExtensionDefinition>> itemExtensions) {
        List<String> parts = new ArrayList<>();
        blocks.values().stream()
                .sorted(Comparator.comparing(BlockDefinition::getId))
                .forEach(definition -> parts.add("block:" + definition.getId()
                        + ":" + definition.getCustomModelData()
                        + ":" + definition.getBaseMaterial()
                        + ":" + definition.getRenderMaterial()
                        + ":" + definition.getExtensionId()
                        + ":" + definition.getTopTexture()
                        + ":" + definition.getSideTexture()
                        + ":" + definition.getBottomTexture()
                        + ":" + definition.getTextureFallback()));
        items.values().stream()
                .sorted(Comparator.comparing(ItemDefinition::getId))
                .forEach(definition -> parts.add("item:" + definition.getId()
                        + ":" + definition.getCustomModelData()
                        + ":" + definition.getBaseMaterial()
                        + ":" + definition.getExtensionId()
                        + ":" + definition.getIconTexture()
                        + ":" + definition.getTextureFallback()));
        extensionJarParts(blockExtensions, "block-jar").forEach(parts::add);
        extensionJarParts(itemExtensions, "item-jar").forEach(parts::add);
        return String.join("|", parts);
    }

    private static List<String> extensionJarParts(List<? extends LoadedExtension<? extends ExtensionDefinition>> extensions,
                                                  String prefix) {
        return extensions.stream()
                .sorted(Comparator.comparing(extension -> extension.getManifest().getId()))
                .map(extension -> prefix + ":" + extension.getManifest().getId()
                        + ":" + extension.getJarFile().lastModified())
                .toList();
    }
}
