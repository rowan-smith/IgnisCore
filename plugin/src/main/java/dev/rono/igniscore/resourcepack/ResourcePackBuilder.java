package dev.rono.igniscore.resourcepack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.loader.ExtensionResourceProvider;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ResourcePackBuilder {
    private final Main plugin;
    private final ItemManager itemManager;
    private final ExtensionResourceProvider resourceProvider;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static class PackResult {
        private final File file;
        private final String hash;

        public PackResult(File file, String hash) {
            this.file = file;
            this.hash = hash;
        }

        public File getFile() { return file; }
        public String getHash() { return hash; }
    }

    public ResourcePackBuilder(Main plugin, ItemManager itemManager, ExtensionResourceProvider resourceProvider) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.resourceProvider = resourceProvider;
    }

    public PackResult buildPack(Map<String, BlockDefinition> blockDefinitions,
                                Map<String, ItemDefinition> itemDefinitions) throws IOException {
        List<CompiledBlockAsset> compiledBlockAssets = compileBlocks(blockDefinitions);
        List<CompiledItemAsset> compiledItemAssets = compileItems(itemDefinitions);
        return packageAssets(compiledBlockAssets, compiledItemAssets);
    }

    private List<CompiledBlockAsset> compileBlocks(Map<String, BlockDefinition> definitions) {
        List<CompiledBlockAsset> assets = new ArrayList<>();
        for (BlockDefinition def : definitions.values()) {
            try {
                assets.add(compileAsset(def));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to compile asset for block " + def.getId() + ": " + e.getMessage());
            }
        }
        return assets;
    }

    private List<CompiledItemAsset> compileItems(Map<String, ItemDefinition> definitions) {
        List<CompiledItemAsset> assets = new ArrayList<>();
        for (ItemDefinition def : definitions.values()) {
            try {
                assets.add(compileItemAsset(def));
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to compile asset for item " + def.getId() + ": " + e.getMessage());
            }
        }
        return assets;
    }

    private CompiledItemAsset compileItemAsset(ItemDefinition def) {
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "minecraft:item/generated");
        JsonObject texturesJson = new JsonObject();
        texturesJson.addProperty("layer0", "igniscore:item/" + def.getId());
        itemModel.add("textures", texturesJson);

        return new CompiledItemAsset(
                def.getId(),
                def.getBaseMaterial(),
                def.getCustomModelData(),
                def.getIconTexture(),
                itemModel
        );
    }

    private CompiledBlockAsset compileAsset(BlockDefinition def) {
        Map<String, String> textures = new LinkedHashMap<>();
        textures.put("top", def.getTopTexture());
        textures.put("bottom", def.getBottomTexture());

        JsonObject blockModel = new JsonObject();
        JsonObject texturesJson = new JsonObject();
        String textureBase = "igniscore:block/" + def.getId();

        if (def.hasPerSideTextures()) {
            blockModel.addProperty("parent", "minecraft:block/cube");
            for (int face = 1; face <= 4; face++) {
                String slot = "side-" + face;
                textures.put(slot, def.getResolvedSideTexture(face));
            }
            texturesJson.addProperty("particle", textureBase + "/side-1");
            texturesJson.addProperty("up", textureBase + "/top");
            texturesJson.addProperty("down", textureBase + "/bottom");
            texturesJson.addProperty("north", textureBase + "/side-1");
            texturesJson.addProperty("east", textureBase + "/side-2");
            texturesJson.addProperty("south", textureBase + "/side-3");
            texturesJson.addProperty("west", textureBase + "/side-4");
        } else {
            textures.put("side", def.getSideTexture());
            blockModel.addProperty("parent", "minecraft:block/cube_bottom_top");
            texturesJson.addProperty("particle", textureBase + "/side");
            texturesJson.addProperty("top", textureBase + "/top");
            texturesJson.addProperty("bottom", textureBase + "/bottom");
            texturesJson.addProperty("side", textureBase + "/side");
        }
        blockModel.add("textures", texturesJson);

        // Add display settings for ItemDisplay consistency
        JsonObject display = new JsonObject();
        
        JsonObject gui = new JsonObject();
        JsonArray guiRotation = new JsonArray();
        guiRotation.add(30); guiRotation.add(225); guiRotation.add(0);
        gui.add("rotation", guiRotation);
        JsonArray guiScale = new JsonArray();
        guiScale.add(0.625); guiScale.add(0.625); guiScale.add(0.625);
        gui.add("scale", guiScale);
        display.add("gui", gui);

        JsonObject fixed = new JsonObject();
        JsonArray fixedScale = new JsonArray();
        fixedScale.add(1.0); fixedScale.add(1.0); fixedScale.add(1.0);
        fixed.add("scale", fixedScale);
        display.add("fixed", fixed);

        JsonObject ground = new JsonObject();
        JsonArray groundScale = new JsonArray();
        groundScale.add(0.5); groundScale.add(0.5); groundScale.add(0.5);
        ground.add("scale", groundScale);
        display.add("ground", ground);

        blockModel.add("display", display);

        // Item Model: assets/igniscore/models/item/<id>.json
        // Use the block model as parent to show 3D block in inventory
        JsonObject itemModel = new JsonObject();
        itemModel.addProperty("parent", "igniscore:block/" + def.getId());

        return new CompiledBlockAsset(
                def.getId(),
                def.getBaseMaterial(),
                def.getRenderMaterial(),
                def.getCustomModelData(),
                textures,
                blockModel,
                itemModel
        );
    }

    private static class OverrideEntry {
        int cmd;
        String model;
        String blockId;
    }

    private static class CompiledItemAsset {
        private final String id;
        private final String baseMaterial;
        private final int customModelData;
        private final String iconTexture;
        private final JsonObject itemModel;

        private CompiledItemAsset(String id, String baseMaterial, int customModelData, String iconTexture, JsonObject itemModel) {
            this.id = id;
            this.baseMaterial = baseMaterial;
            this.customModelData = customModelData;
            this.iconTexture = iconTexture;
            this.itemModel = itemModel;
        }

        public String getId() {
            return id;
        }

        public String getBaseMaterial() {
            return baseMaterial;
        }

        public int getCustomModelData() {
            return customModelData;
        }

        public String getIconTexture() {
            return iconTexture;
        }

        public JsonObject getItemModel() {
            return itemModel;
        }
    }

    private PackResult packageAssets(List<CompiledBlockAsset> assets, List<CompiledItemAsset> itemAssets) throws IOException {
        Path tempDir = Files.createTempDirectory("igniscore_rp");
        try {
            // pack.mcmeta
            writePackMeta(tempDir);

            // Group by material for item overrides (both base and render materials)
            Map<String, List<OverrideEntry>> materialOverrides = new HashMap<>();
            
            for (CompiledBlockAsset asset : assets) {
                // Write block model: assets/igniscore/models/block/<id>.json
                Path blockModelPath = tempDir.resolve("assets/igniscore/models/block/" + asset.getId() + ".json");
                Files.createDirectories(blockModelPath.getParent());
                Files.writeString(blockModelPath, gson.toJson(asset.getBlockModel()));
                plugin.getLogger().info("Generated block model: " + blockModelPath.toString().replace("\\", "/"));

                // Write item model: assets/igniscore/models/item/<id>.json
                Path itemModelPath = tempDir.resolve("assets/igniscore/models/item/" + asset.getId() + ".json");
                Files.createDirectories(itemModelPath.getParent());
                Files.writeString(itemModelPath, gson.toJson(asset.getItemModel()));
                plugin.getLogger().info("Generated item model: " + itemModelPath.toString().replace("\\", "/"));

                // Modern item definition: assets/igniscore/items/<id>.json
                Path itemDefinitionPath = tempDir.resolve("assets/igniscore/items/" + asset.getId() + ".json");
                Files.createDirectories(itemDefinitionPath.getParent());
                Files.writeString(itemDefinitionPath, gson.toJson(createModelItemDefinition("igniscore:item/" + asset.getId())));
                plugin.getLogger().info("Generated item definition: " + itemDefinitionPath.toString().replace("\\", "/"));

                // Write textures: assets/igniscore/textures/block/<id>/
                Path textureDir = tempDir.resolve("assets/igniscore/textures/block/" + asset.getId());
                Files.createDirectories(textureDir);
                copyTextures(asset, textureDir);

                // Inventory entry (baseMaterial)
                OverrideEntry invEntry = new OverrideEntry();
                invEntry.cmd = asset.getCustomModelData();
                invEntry.model = "igniscore:item/" + asset.getId();
                invEntry.blockId = asset.getId();
                materialOverrides.computeIfAbsent(asset.getBaseMaterial(), k -> new ArrayList<>()).add(invEntry);

                // Render entry (renderMaterial) - only if different
                if (!asset.getRenderMaterial().equals(asset.getBaseMaterial())) {
                    OverrideEntry renderEntry = new OverrideEntry();
                    renderEntry.cmd = asset.getCustomModelData();
                    renderEntry.model = "igniscore:item/" + asset.getId();
                    renderEntry.blockId = asset.getId();
                    materialOverrides.computeIfAbsent(asset.getRenderMaterial(), k -> new ArrayList<>()).add(renderEntry);
                } else {
                    plugin.getLogger().warning("Block " + asset.getId() + " uses same material for base and render (" + asset.getBaseMaterial() + "). Inventory and world look will be the same (2D icon). Use different materials to decouple.");
                }
            }

            // Write item overrides: assets/minecraft/models/item/<base_item>.json
            for (CompiledItemAsset itemAsset : itemAssets) {
                Path itemModelPath = tempDir.resolve("assets/igniscore/models/item/" + itemAsset.getId() + ".json");
                Files.createDirectories(itemModelPath.getParent());
                Files.writeString(itemModelPath, gson.toJson(itemAsset.getItemModel()));

                Path itemDefinitionPath = tempDir.resolve("assets/igniscore/items/" + itemAsset.getId() + ".json");
                Files.createDirectories(itemDefinitionPath.getParent());
                Files.writeString(itemDefinitionPath, gson.toJson(createModelItemDefinition("igniscore:item/" + itemAsset.getId())));

                Path texturePath = tempDir.resolve("assets/igniscore/textures/item/" + itemAsset.getId() + ".png");
                Files.createDirectories(texturePath.getParent());
                copyItemTexture(itemAsset, texturePath);

                OverrideEntry entry = new OverrideEntry();
                entry.cmd = itemAsset.getCustomModelData();
                entry.model = "igniscore:item/" + itemAsset.getId();
                entry.blockId = itemAsset.getId();
                materialOverrides.computeIfAbsent(itemAsset.getBaseMaterial(), k -> new ArrayList<>()).add(entry);
            }

            for (Map.Entry<String, List<OverrideEntry>> entry : materialOverrides.entrySet()) {
                String material = entry.getKey();
                List<OverrideEntry> overridesList = entry.getValue();
                writeItemOverride(tempDir, material, overridesList);
                writeModernItemDefinition(tempDir, material, overridesList);
            }

            // Zip it
            Path tempZip = Files.createTempFile("rp_temp", ".zip");
            zip(tempDir, tempZip);
            
            String hash;
            try {
                hash = calculateHash(tempZip.toFile());
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("Hash algorithm not found", e);
            }
            
            File packsDir = new File(plugin.getDataFolder(), "packs");
            if (!packsDir.exists()) packsDir.mkdirs();
            
            File finalZip = new File(packsDir, "resourcepack_" + hash + ".zip");
            Files.move(tempZip, finalZip.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            plugin.getLogger().info("Final pack path: " + finalZip.getAbsolutePath());
            plugin.getLogger().info("Final pack hash: " + hash);

            return new PackResult(finalZip, hash);
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    private String calculateHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void writePackMeta(Path tempDir) throws IOException {
        JsonObject pack = new JsonObject();
        JsonObject meta = new JsonObject();
        meta.addProperty("pack_format", 34);
        meta.addProperty("description", "IgnisCore Custom Blocks Pack");
        pack.add("pack", meta);
        Files.writeString(tempDir.resolve("pack.mcmeta"), gson.toJson(pack));
    }

    private void writeItemOverride(Path tempDir, String baseItem, List<OverrideEntry> overridesList) throws IOException {
        Path overridePath = tempDir.resolve("assets/minecraft/models/item/" + baseItem + ".json");
        Files.createDirectories(overridePath.getParent());

        JsonObject root = new JsonObject();
        String parent = baseItem.equalsIgnoreCase("carrot_on_a_stick") ? "minecraft:item/handheld" : "minecraft:item/generated";
        root.addProperty("parent", parent);
        root.addProperty("gui_light", "front");

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "minecraft:item/" + baseItem.toLowerCase());
        root.add("textures", textures);

        JsonArray overridesArray = new JsonArray();
        overridesList.sort(Comparator.comparingInt(o -> o.cmd));
        
        for (OverrideEntry entry : overridesList) {
            JsonObject override = new JsonObject();
            JsonObject predicate = new JsonObject();
            predicate.addProperty("custom_model_data", entry.cmd);
            override.add("predicate", predicate);
            override.addProperty("model", entry.model);
            overridesArray.add(override);
            plugin.getLogger().info("Registered override in " + baseItem + ".json: CMD " + entry.cmd + " -> " + entry.model + " (Block: " + entry.blockId + ")");
        }
        root.add("overrides", overridesArray);

        Files.writeString(overridePath, gson.toJson(root));
        plugin.getLogger().info("Created " + overridePath.toString().replace("\\", "/") + " with " + overridesArray.size() + " overrides.");
        
        if (plugin.isDebugEnabled()) {
             plugin.getLogger().info("Generated JSON for " + baseItem + ":\n" + gson.toJson(root));
        }
    }

    private JsonObject createModelItemDefinition(String modelPath) {
        JsonObject root = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", modelPath);
        root.add("model", model);
        return root;
    }

    private void writeModernItemDefinition(Path tempDir, String baseItem, List<OverrideEntry> overridesList) throws IOException {
        Path itemDefinitionPath = tempDir.resolve("assets/minecraft/items/" + baseItem + ".json");
        Files.createDirectories(itemDefinitionPath.getParent());

        JsonObject root = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:range_dispatch");
        model.addProperty("property", "minecraft:custom_model_data");
        model.addProperty("index", 0);

        JsonArray entries = new JsonArray();
        overridesList.sort(Comparator.comparingInt(o -> o.cmd));

        for (OverrideEntry entry : overridesList) {
            JsonObject rangeEntry = new JsonObject();
            rangeEntry.addProperty("threshold", entry.cmd);
            JsonObject entryModel = new JsonObject();
            entryModel.addProperty("type", "minecraft:model");
            entryModel.addProperty("model", entry.model);
            rangeEntry.add("model", entryModel);
            entries.add(rangeEntry);
            plugin.getLogger().info("Registered modern item definition in " + baseItem + ".json: CMD " + entry.cmd + " -> " + entry.model + " (Block: " + entry.blockId + ")");
        }

        model.add("entries", entries);
        model.add("fallback", createModelItemDefinition("minecraft:item/" + baseItem).getAsJsonObject("model"));
        root.add("model", model);

        Files.writeString(itemDefinitionPath, gson.toJson(root));
        plugin.getLogger().info("Created " + itemDefinitionPath.toString().replace("\\", "/") + " with " + entries.size() + " modern CMD entries.");

        if (plugin.isDebugEnabled()) {
            plugin.getLogger().info("Generated modern item definition JSON for " + baseItem + ":\n" + gson.toJson(root));
        }
    }

    private void copyTextures(CompiledBlockAsset asset, Path destDir) throws IOException {
        for (Map.Entry<String, String> entry : asset.getTextures().entrySet()) {
            String key = entry.getKey();
            String fileName = entry.getValue();
            
            try (InputStream is = getTextureStream(asset.getId(), fileName)) {
                if (is != null) {
                    Path texturePath = destDir.resolve(key + ".png");
                    TextureFileWriter.writePackTexture(is, fileName, texturePath);
                    plugin.getLogger().info("Generated texture: " + texturePath.toString().replace("\\", "/"));
                } else {
                    String error = "CRITICAL: Texture missing for block " + asset.getId() + ": " + fileName;
                    plugin.getLogger().severe(error);
                    throw new IOException(error);
                }
            }
        }
    }

    private InputStream getTextureStream(String blockId, String fileName) throws IOException {
        BlockDefinition definition = plugin.getBlockManager().getBlockTypes().get(blockId);
        if (definition != null) {
            InputStream extensionStream = resourceProvider.getBlockTextureStream(definition, fileName);
            if (extensionStream != null) {
                return extensionStream;
            }
        }
        return null;
    }

    private void copyItemTexture(CompiledItemAsset asset, Path texturePath) throws IOException {
        ItemDefinition definition = itemManager.getItemTypes().get(asset.getId());
        if (definition == null) {
            throw new IOException("Unknown item definition for texture copy: " + asset.getId());
        }

        try (InputStream inputStream = resourceProvider.getItemTextureStream(definition, asset.getIconTexture())) {
            if (inputStream == null) {
                throw new IOException("Missing texture " + asset.getIconTexture() + " for item " + asset.getId());
            }
            TextureFileWriter.writePackTexture(inputStream, asset.getIconTexture(), texturePath);
        }
    }

    private void zip(Path sourceDirPath, Path zipFilePath) throws IOException {
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipFilePath));
             java.util.stream.Stream<Path> paths = Files.walk(sourceDirPath)) {
            paths.filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString().replace("\\", "/"));
                    try {
                        zs.putNextEntry(zipEntry);
                        Files.copy(path, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        plugin.getLogger().severe("Failed to add entry to zip: " + zipEntry.getName());
                    }
                });
        }
    }

    private void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }
}
