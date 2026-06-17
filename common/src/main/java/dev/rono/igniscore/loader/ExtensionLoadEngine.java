package dev.rono.igniscore.loader;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.IgnisApiVersion;
import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionRequirements;
import dev.rono.igniscore.api.extension.ExtensionRuntimeCapabilities;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.api.integration.IgnisIntegrationRegistry;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ExtensionDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.common.runtime.IgnisRuntimeHost;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Singleton
public final class ExtensionLoadEngine {
    private final IgnisRuntimeHost host;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final IgnisIntegrationRegistry integrationRegistry;

    @Inject
    ExtensionLoadEngine(IgnisRuntimeHost host,
                        IgnisStrategyRegistry strategyRegistry,
                        IgnisStrategyContext strategyContext,
                        IgnisIntegrationRegistry integrationRegistry) {
        this.host = host;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
        this.integrationRegistry = integrationRegistry;
    }

    List<LoadedExtension<BlockDefinition>> loadBlocks() {
        return loadAll(ExtensionKind.BLOCK);
    }

    List<LoadedExtension<ItemDefinition>> loadItems() {
        return loadAll(ExtensionKind.ITEM);
    }

    void unload(List<? extends LoadedExtension<?>> extensions) {
        for (LoadedExtension<?> extension : extensions) {
            strategyRegistry.unregisterBySource(extension.getManifest().getId());
            try {
                extension.getClassLoader().close();
            } catch (Exception error) {
                host.getLogger().log(Level.WARNING,
                        "Failed to close classloader for extension " + extension.getManifest().getId()
                                + " (" + extension.getJarFile().getName() + ")",
                        error);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <D extends ExtensionDefinition> List<LoadedExtension<D>> loadAll(ExtensionKind kind) {
        File folder = prepareFolder(kind);
        if (folder == null) {
            return List.of();
        }

        List<LoadedExtension<D>> loaded = new ArrayList<>();
        int modelData = kind.modelDataStart();
        for (File jar : listJars(folder, kind)) {
            try {
                loaded.add((LoadedExtension<D>) loadJar(jar, modelData++, kind));
            } catch (Exception e) {
                host.getLogger().severe("Failed to load " + kind.folderName() + " extension "
                        + jar.getName() + ": " + e.getMessage());
            }
        }
        return List.copyOf(loaded);
    }

    private File prepareFolder(ExtensionKind kind) {
        File folder = host.getDataDirectory().resolve(kind.folderName()).toFile();

        if (!folder.exists() && !folder.mkdirs()) {
            host.getLogger().warning("Could not create " + kind.folderName() + " folder at " + folder.getAbsolutePath());
            return null;
        }
        return folder;
    }

    private List<File> listJars(File folder, ExtensionKind kind) {
        File[] jars = folder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            host.getLogger().info("No " + kind.folderName() + " extension JARs found in " + folder.getAbsolutePath());
            return List.of();
        }
        return Arrays.stream(jars)
                .sorted(Comparator.comparing(File::getName))
                .toList();
    }

    private <D extends ExtensionDefinition> LoadedExtension<D> loadJar(File jarFile, int modelData, ExtensionKind kind)
            throws Exception {
        ExtensionJarSupport.JarMetadata<ExtensionManifest> metadata = ExtensionJarSupport.readExtensionMetadata(
                jarFile,
                kind.manifestFileName());
        ExtensionManifest manifest = metadata.manifest();
        Map<String, Object> config = metadata.config();
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);
        @SuppressWarnings("unchecked")
        D definition = (D) kind.parseDefinition(config, manifest.getId(), modelData, manifest.getId());
        return loadExtension(jarFile, manifest, descriptor, definition, kind);
    }

    private <D extends ExtensionDefinition> LoadedExtension<D> loadExtension(File jarFile,
                                                               ExtensionManifest manifest,
                                                               IgnisStrategyDescriptor descriptor,
                                                               D definition,
                                                               ExtensionKind kind) throws Exception {
        IgnisApiVersion.requireCompatible(manifest.getApiVersion(), manifest.getId());
        validateIntegrations(manifest);

        if (strategyContext != null) {
            boolean protocolEnabled = strategyContext.protocol() != null && strategyContext.protocol().isEnabled();
            boolean nbtEnabled = strategyContext.nbt() != null && strategyContext.nbt().isEnabled();
            ExtensionRuntimeCapabilities capabilities = new ExtensionRuntimeCapabilities(protocolEnabled, nbtEnabled);
            for (String warning : ExtensionRequirements.validate(manifest, capabilities, true)) {
                host.getLogger().warning(warning);
            }
        }

        String strategyId = descriptor.getId();

        URLClassLoader classLoader = ExtensionJarSupport.createClassLoader(jarFile, host.getExtensionParentClassLoader());
        ExtensionResources resources = new ExtensionResources(classLoader);

        try {
            ExtensionJarSupport.loadStrategy(classLoader, manifest.getStrategyClass(), strategyContext,
                    strategyRegistry, descriptor, kind);

            if (!strategyRegistry.isRegistered(strategyId)) {
                throw new IllegalStateException(kind.folderName() + " extension " + manifest.getId()
                        + " did not register strategy class " + manifest.getStrategyClass());
            }

            host.debug("Loaded " + kind.folderName() + " extension '" + manifest.getName() + "' v"
                    + manifest.getVersion() + " (" + definition.getId() + ") from " + jarFile.getName());
            return new LoadedExtension<>(manifest, jarFile, classLoader, definition, resources);
        } catch (Exception e) {
            classLoader.close();
            throw e;
        }
    }

    private void validateIntegrations(ExtensionManifest manifest) {
        for (String integrationId : manifest.getRequiredIntegrations()) {
            if (!integrationRegistry.isEnabled(integrationId)) {
                throw new IllegalStateException("Extension " + manifest.getId()
                        + " requires integration '" + integrationId + "' (provider: "
                        + integrationRegistry.providerName(integrationId) + ")");
            }
        }
    }
}
