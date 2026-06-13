package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ExtensionResourceProvider {
    private final Map<String, LoadedExtension<BlockDefinition>> blockExtensionsById = new HashMap<>();
    private final Map<String, LoadedExtension<ItemDefinition>> itemExtensionsById = new HashMap<>();

    public void setBlockExtensions(List<LoadedExtension<BlockDefinition>> extensions) {
        blockExtensionsById.clear();
        for (LoadedExtension<BlockDefinition> extension : extensions) {
            blockExtensionsById.put(extension.getDefinition().getId(), extension);
        }
    }

    public void setItemExtensions(List<LoadedExtension<ItemDefinition>> extensions) {
        itemExtensionsById.clear();
        for (LoadedExtension<ItemDefinition> extension : extensions) {
            itemExtensionsById.put(extension.getDefinition().getId(), extension);
        }
    }

    public InputStream getBlockTextureStream(BlockDefinition definition, String fileName) {
        LoadedExtension<BlockDefinition> extension = blockExtensionsById.get(definition.getId());
        if (extension == null) {
            return null;
        }

        InputStream stream = extension.getResources().open("textures/" + fileName);
        if (stream != null) {
            return stream;
        }

        return extension.getResources().open(fileName);
    }

    public InputStream getItemTextureStream(ItemDefinition definition, String fileName) {
        LoadedExtension<ItemDefinition> extension = itemExtensionsById.get(definition.getId());
        if (extension == null) {
            return null;
        }

        InputStream stream = extension.getResources().open("textures/" + fileName);
        if (stream != null) {
            return stream;
        }

        return extension.getResources().open(fileName);
    }
}
