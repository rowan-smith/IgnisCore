package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.model.ExtensionDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;

import java.util.Map;

enum ExtensionKind {
    BLOCK("blocks", "bundled/blocks", "block-extension.yml", 10001) {
        @Override
        ExtensionDefinition parseDefinition(Map<String, Object> config, String defaultId, int modelData, String extensionId) {
            return DefinitionParser.parseBlock(config, defaultId, modelData, extensionId);
        }

        @Override
        Class<? extends IgnisStrategy> strategyType() {
            return IgnisBlockStrategy.class;
        }
    },
    ITEM("items", "bundled/items", "item-extension.yml", 20001) {
        @Override
        ExtensionDefinition parseDefinition(Map<String, Object> config, String defaultId, int modelData, String extensionId) {
            return DefinitionParser.parseItem(config, defaultId, modelData, extensionId);
        }

        @Override
        Class<? extends IgnisStrategy> strategyType() {
            return IgnisItemStrategy.class;
        }
    };

    private final String folderName;
    private final String bundledResourcePrefix;
    private final String manifestFileName;
    private final int modelDataStart;

    ExtensionKind(String folderName, String bundledResourcePrefix, String manifestFileName, int modelDataStart) {
        this.folderName = folderName;
        this.bundledResourcePrefix = bundledResourcePrefix;
        this.manifestFileName = manifestFileName;
        this.modelDataStart = modelDataStart;
    }

    String folderName() {
        return folderName;
    }

    String bundledResourcePrefix() {
        return bundledResourcePrefix;
    }

    String manifestFileName() {
        return manifestFileName;
    }

    int modelDataStart() {
        return modelDataStart;
    }

    abstract ExtensionDefinition parseDefinition(Map<String, Object> config, String defaultId, int modelData, String extensionId);

    abstract Class<? extends IgnisStrategy> strategyType();
}
