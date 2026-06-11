package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.ItemDefinition;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ExtensionResourceProvider {
    private final Map<String, LoadedBlockExtension> blockExtensionsById = new HashMap<>();
    private final Map<String, LoadedItemExtension> itemExtensionsById = new HashMap<>();

    public void setBlockExtensions(List<LoadedBlockExtension> extensions) {
        blockExtensionsById.clear();
        for (LoadedBlockExtension extension : extensions) {
            blockExtensionsById.put(extension.getBlockDefinition().getId(), extension);
        }
    }

    public void setItemExtensions(List<LoadedItemExtension> extensions) {
        itemExtensionsById.clear();
        for (LoadedItemExtension extension : extensions) {
            itemExtensionsById.put(extension.getItemDefinition().getId(), extension);
        }
    }

    public InputStream getBlockTextureStream(BlockDefinition definition, String fileName) {
        LoadedBlockExtension extension = blockExtensionsById.get(definition.getId());
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
        LoadedItemExtension extension = itemExtensionsById.get(definition.getId());
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
