package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ExtensionDefinition;
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
        indexExtensions(extensions, blockExtensionsById);
    }

    public void setItemExtensions(List<LoadedExtension<ItemDefinition>> extensions) {
        indexExtensions(extensions, itemExtensionsById);
    }

    public InputStream getBlockTextureStream(BlockDefinition definition, String fileName) {
        return getTextureStream(definition, blockExtensionsById, fileName);
    }

    public InputStream getItemTextureStream(ItemDefinition definition, String fileName) {
        return getTextureStream(definition, itemExtensionsById, fileName);
    }

    private static <D extends ExtensionDefinition> void indexExtensions(
            List<LoadedExtension<D>> extensions,
            Map<String, LoadedExtension<D>> target) {
        target.clear();
        for (LoadedExtension<D> extension : extensions) {
            target.put(extension.getDefinition().getId(), extension);
        }
    }

    private static <D extends ExtensionDefinition> InputStream getTextureStream(
            D definition,
            Map<String, LoadedExtension<D>> index,
            String fileName) {
        LoadedExtension<D> extension = index.get(definition.getId());
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
